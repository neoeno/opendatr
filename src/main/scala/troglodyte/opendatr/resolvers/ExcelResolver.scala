package troglodyte.opendatr.resolvers

import scala.collection.JavaConverters._
import java.io.File

import org.apache.poi.ss.usermodel.{Cell, DateUtil, WorkbookFactory}
import troglodyte.opendatr.{Dataset, Entity}

class ExcelResolver extends Resolver {
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
    Some(new Dataset(firstSheet.rowIterator.asScala.map(row => {
      new Entity(row.cellIterator.asScala.map(cell => {
        (cell.getColumnIndex.toString,
          cell.getCellType match {
            case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
            case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
            case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
            case Cell.CELL_TYPE_FORMULA => ??? // Switch on cell.getCachedFormulaResultType to get this
            case _ => null
          }
        )
      }).toMap)
    }).toList))
  }
}
