package troglodyte.opendatr.determiners

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Sheet}
import troglodyte.opendatr.askers.Asker

import scala.collection.JavaConverters._

class WorkbookComponentDeterminer(asker: Asker) {
  val rowSearchRange = 10

  def determineHeadings(sheet: Sheet): (Option[Int], Map[Int, String]) = {
    new SpreadsheetHeadingsDeterminer(asker).determineHeadings(
      convertSheetToIterables(sheet).take(rowSearchRange).toList
    ).map(headingsRowNumber => {
      (Some(headingsRowNumber), generateHeadingsFromRow(sheet, headingsRowNumber))
    }).getOrElse(
      (None, generateHeadingsMapFromColumnIndexes(sheet))
    )
  }

  def getCellValue(cell: Cell): Any = {
    cell.getCellType match {
      case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
      case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
      case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
      case Cell.CELL_TYPE_BLANK => ""
      case Cell.CELL_TYPE_FORMULA => cell.getCachedFormulaResultType match {
        case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
        case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
        case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
        case _ => throw new IllegalArgumentException(s"Can't get value of cell of type ${cell.getCellType}")
      }
      case _ => throw new IllegalArgumentException(s"Can't get value of cell of type ${cell.getCellType}")
    }
  }

  private def generateHeadingsMapFromColumnIndexes(sheet: Sheet): Map[Int, String] = {
    sheet.getRow(sheet.getFirstRowNum)
      .cellIterator.asScala
      .map(cell => (cell.getColumnIndex, cell.getColumnIndex.toString))
      .toMap
  }

  private def generateHeadingsFromRow(sheet: Sheet, rowNumber: Int): Map[Int, String] = {
    sheet.getRow(rowNumber)
      .cellIterator.asScala
      .map(cell => (cell.getColumnIndex, getCellValue(cell).toString))
      .filter(_._2.nonEmpty)
      .toMap
  }

  private def convertSheetToIterables(sheet: Sheet): Iterator[(List[Any], Int)] = {
    sheet.rowIterator.asScala.map(
      row => (
        row.cellIterator.asScala.map(
          cell => getCellValue(cell)).toList,
        row.getRowNum
      )
    )
  }
}
