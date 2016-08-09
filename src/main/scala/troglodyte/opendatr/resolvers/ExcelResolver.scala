package troglodyte.opendatr.resolvers

import scala.collection.JavaConverters._
import java.io.File

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Sheet, WorkbookFactory}
import troglodyte.opendatr.askers.Asker
import troglodyte.opendatr.{Dataset, Entity}

class ExcelResolver(asker: Asker) extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case file: File =>
      try {
        WorkbookFactory.create(file)
        true
      } catch {
        case _: Exception => false
      }
    case _ => false
  }

  override def resolve(puzzle: Any): Option[Any] = {
    val file = puzzle.asInstanceOf[File]
    val workbookFactory = WorkbookFactory.create(file)
    val firstSheet = workbookFactory.getSheetAt(0)
    val (ignoreRow, columnsToHeadings) = getColumnsToHeadingsMap(firstSheet)
    Some(new Dataset(firstSheet.rowIterator.asScala
      .filterNot(row => ignoreRow.contains(row.getRowNum)) // Ignore heading row
      .map(row => {
        new Entity(row.cellIterator.asScala.map(cell =>
          (columnsToHeadings(cell.getColumnIndex), getCellValue(cell))).toMap)
    }).toList))
  }

  def getCellValue(cell: Cell): Any = {
    cell.getCellType match {
      case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
      case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
      case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
      case Cell.CELL_TYPE_FORMULA => ??? // Switch on cell.getCachedFormulaResultType to get this
      case _ => null
    }
  }

  private def getColumnsToHeadingsMap(sheet: Sheet): (Option[Int], Map[Int, String]) = {
    asker.show(sheet.getRow(sheet.getFirstRowNum).cellIterator.asScala.map(cell => getCellValue(cell).toString).mkString(", "))
    if (asker.askYesNo('has_headings, "Do these look like column headings to you?")) {
      (Some(sheet.getFirstRowNum), sheet.getRow(sheet.getFirstRowNum).cellIterator.asScala.map(cell => (cell.getColumnIndex, getCellValue(cell).toString)).toMap)
    } else {
      (None, sheet.getRow(sheet.getFirstRowNum).cellIterator.asScala.map(cell => (cell.getColumnIndex, cell.getColumnIndex.toString)).toMap)
    }
  }
}
