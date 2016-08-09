package troglodyte.opendatr.resolvers

import java.io.{File, FileOutputStream, FileWriter}

import org.apache.poi.hssf.usermodel.HSSFWorkbook
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
        val resolver = new ExcelResolver(new PresetMapAsker(Map('has_headings -> true)))

        it("returns a Dataset with the values associated with columns") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length == 2)
          assert(dataset.getEntities(0).getValues == Map("col1" -> 1, "col2" -> 2))
          assert(dataset.getEntities(1).getValues == Map("col1" -> 3, "col2" -> 4))
        }
      }

      describe("when the user indicates no column headings") {
        val resolver = new ExcelResolver(new PresetMapAsker(Map('has_headings -> false)))

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
