package troglodyte.opendatr.determiners

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Row, Sheet}
import troglodyte.opendatr.askers.Asker

import scala.collection.JavaConverters._

class WorkbookComponentDeterminer(asker: Asker) {
  val rowSearchRange = 10

  def sheetToMaps(sheet: Sheet): Iterator[Map[String, Any]] = {
    val headingsRowNum = determineHeadingsRowNum(sheet)
    val headingsIndex = makeColumnIndexToHeadingsMap(sheet, headingsRowNum)
    eliminateBlankRows(rowsAfterRowNum(sheet, headingsRowNum))
      .map(rowToMap(headingsIndex, _))
  }

  private def getCellValue(cell: Cell): Any = {
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

  private def rowToMap(headingsIndex: Map[Int, String], row: Row): Map[String, Any] = {
    row.cellIterator.asScala
      .filter(cell => headingsIndex.contains(cell.getColumnIndex))
      .map(cell =>
        (headingsIndex(cell.getColumnIndex), getCellValue(cell))
      ).toMap
  }

  private def eliminateBlankRows(rows: Iterator[Row]) = {
    rows.filter(_.cellIterator.asScala.map(getCellValue).mkString.trim.nonEmpty)
  }

  private def rowsAfterRowNum(sheet: Sheet, rowNum: Option[Int]): Iterator[Row] = {
    if (rowNum.isEmpty) {
      sheet.rowIterator.asScala
    } else {
      sheet.rowIterator.asScala.filter(row => rowNum.exists(_ < row.getRowNum))
    }
  }

  private def determineHeadingsRowNum(sheet: Sheet): Option[Int] = {
    new SpreadsheetHeadingsDeterminer(asker).determineHeadings(
      convertSheetToIterables(sheet).take(rowSearchRange).toList
    )
  }

  private def makeColumnIndexToHeadingsMap(sheet: Sheet, maybeHeadingsRowNum: Option[Int]): Map[Int, String] = {
    maybeHeadingsRowNum.map(headingsRowNumber =>
      // if we have a row, use that for the map
      generateHeadingsFromRow(sheet, headingsRowNumber)
    ).getOrElse(
      // otherwise, use sequential ints as our column headings
      generateHeadingsMapFromColumnIndexes(sheet)
    )
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
