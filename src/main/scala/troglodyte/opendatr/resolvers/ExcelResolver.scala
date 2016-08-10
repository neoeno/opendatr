package troglodyte.opendatr.resolvers

import java.io.File

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Sheet, WorkbookFactory}
import troglodyte.opendatr.askers.Asker
import troglodyte.opendatr.determiners.SpreadsheetHeadingsDeterminer
import troglodyte.opendatr.utils.ResolverUtils
import troglodyte.opendatr.{Dataset, Entity}

import scala.collection.JavaConverters._

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
    ResolverUtils.withClosing(WorkbookFactory.create(file)) { workbookFactory =>
      val firstSheet = workbookFactory.getSheetAt(0)
      val (ignoreRow, columnsToHeadings) = getColumnsToHeadingsMap(firstSheet)
      Some(new Dataset(firstSheet.rowIterator.asScala
        .filter(row => ignoreRow.isEmpty || ignoreRow.exists(_ < row.getRowNum)) // Select only rows after the heading
        .map(row => {
          new Entity(row.cellIterator.asScala.map(cell =>
            (columnsToHeadings(cell.getColumnIndex), getCellValue(cell))
          ).toMap)
        }).toList
      ))
    }
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

  private def getColumnsToHeadingsMap(sheet: Sheet): (Option[Int], Map[Int, String]) = {
    val determiner = new SpreadsheetHeadingsDeterminer(asker)
    val headingsRowNumber = determiner.determineHeadings(
      sheet.rowIterator.asScala.take(10).map(
        row => row.cellIterator.asScala.map(
          cell => getCellValue(cell).toString).toList).toList)
    if (headingsRowNumber.nonEmpty) {
      (headingsRowNumber, sheet.getRow(headingsRowNumber.get).cellIterator.asScala.map(cell => (cell.getColumnIndex, getCellValue(cell).toString)).toMap)
    } else {
      (None, sheet.getRow(sheet.getFirstRowNum).cellIterator.asScala.map(cell => (cell.getColumnIndex, cell.getColumnIndex.toString)).toMap)
    }
  }
}
