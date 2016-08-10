package troglodyte.opendatr.resolvers

import java.io.{File, FileOutputStream, FileWriter}

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.scalatest.FunSpec
import troglodyte.opendatr.Dataset
import troglodyte.opendatr.askers.PresetMapAsker

class ExcelResolverTest extends FunSpec {
  val resolver = new ExcelResolver(new PresetMapAsker(Map()))

  describe("given a simple Excel file") {
    val tempFile = File.createTempFile("temp", "file")
    val outputStream = new FileOutputStream(tempFile)
    val workbook = new HSSFWorkbook()
    val sheet = workbook.createSheet("sheet1")
    val headingRow = sheet.createRow(0)
    headingRow.createCell(0).setCellValue("col1")
    headingRow.createCell(1).setCellValue("col2")
    val valueRow1 = sheet.createRow(1)
    valueRow1.createCell(0).setCellValue(1)
    valueRow1.createCell(1).setCellValue(2)
    val valueRow2 = sheet.createRow(2)
    valueRow2.createCell(0).setCellValue(3)
    valueRow2.createCell(1).setCellValue(4)
    workbook.write(outputStream)
    outputStream.close()

    describe("#canResolve") {
      it("is true") {
        assert(resolver.canResolve(tempFile) === true)
      }
    }

    describe("#resolve") {
      describe("when the user indicates column headings") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('pick_sheet -> 0, 'pick_headings_row -> Some(0))))

        it("returns a Dataset with the values associated with columns") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 2)
          assert(dataset.getEntities(0).getValues == Map("col1" -> 1, "col2" -> 2))
          assert(dataset.getEntities(1).getValues == Map("col1" -> 3, "col2" -> 4))
        }
      }

      describe("when the user indicates column headings further down") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('pick_sheet -> 0, 'pick_headings_row -> Some(1))))

        it("returns a Dataset with the values associated with columns, ignoring the rows before the headings") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 1)
          assert(dataset.getEntities(0).getValues == Map("1.0" -> 3, "2.0" -> 4))
        }
      }

      describe("when the user indicates no column headings") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('pick_sheet -> 0, 'pick_headings_row -> None)))

        it("returns a Dataset with the values associated with column indexes") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 3)
          assert(dataset.getEntities(0).getValues == Map("0" -> "col1", "1" -> "col2"))
          assert(dataset.getEntities(1).getValues == Map("0" -> 1, "1" -> 2))
          assert(dataset.getEntities(2).getValues == Map("0" -> 3, "1" -> 4))
        }
      }
    }
  }

  describe("given a multi-sheet Excel file") {
    val tempFile = File.createTempFile("temp", "file")
    val outputStream = new FileOutputStream(tempFile)
    val workbook = new HSSFWorkbook()
    val createSheet = (workbook: Workbook, name: String, rows: List[List[String]]) => {
      val sheet = workbook.createSheet(name)
      val headingRow = sheet.createRow(0)
      rows.foreach(cells => {
        val row = sheet.createRow(sheet.getLastRowNum + 1)
        cells.foreach(cell => {
          row.createCell(row.getLastCellNum + 1).setCellValue(cell)
        })
      })
    }
    createSheet(workbook, "sheet1", List(List("col1", "col2"), List("1", "2")))
    createSheet(workbook, "sheet2", List(List("col3", "col4"), List("3", "4")))
    workbook.write(outputStream)
    outputStream.close()

    describe("#resolve") {
      describe("when the user picks the first sheet") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('pick_sheet -> 0, 'pick_headings_row -> Some(0))))

        it("returns a Dataset with the values from the second sheet") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 1)
          assert(dataset.getEntities(0).getValues == Map("col1" -> "1", "col2" -> "2"))
        }
      }

      describe("when the user picks the second sheet") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('pick_sheet -> 1, 'pick_headings_row -> Some(0))))

        it("returns a Dataset with the values from the second sheet") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 1)
          assert(dataset.getEntities(0).getValues == Map("col3" -> "3", "col4" -> "4"))
        }
      }
    }
  }

  describe("given a random text file") {
    val tempFile = File.createTempFile("temp", "file")
    val writer = new FileWriter(tempFile)
    writer.write("hello world")
    writer.flush()
    writer.close()

    describe("#canResolve") {
      it("is false") {
        assert(resolver.canResolve(tempFile) === false)
      }
    }
  }
}
