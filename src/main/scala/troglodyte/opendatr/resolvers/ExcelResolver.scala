package troglodyte.opendatr.resolvers

import java.io.File

import org.apache.poi.ss.usermodel._
import troglodyte.opendatr.askers.Asker
import troglodyte.opendatr.determiners.WorkbookComponentDeterminer
import troglodyte.opendatr.utils.ResolverUtils
import troglodyte.opendatr.{Dataset, Entity}

import scala.collection.JavaConverters._

class ExcelResolver(asker: Asker) extends Resolver {
  val determiner = new WorkbookComponentDeterminer(asker)

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

  def pickSheet(workbook: Workbook): Sheet = {
    val sheets = workbook.sheetIterator.asScala.toList
    val choice = asker.choose('pick_sheet, "Which of these sheets would you like to export?",
      sheets.map(sheet => sheet.getSheetName))
    workbook.getSheetAt(choice)
  }

  override def resolve(puzzle: Any): Option[Any] = {
    val file = puzzle.asInstanceOf[File]
    ResolverUtils.withClosing(WorkbookFactory.create(file)) { workbook =>
      val sheet = pickSheet(workbook)
      val (ignoreRow, columnsToHeadings) = determiner.determineHeadings(sheet)
      Some(new Dataset(sheet.rowIterator.asScala
        .filter(row => ignoreRow.isEmpty || ignoreRow.exists(_ < row.getRowNum)) // Select only rows after the heading
        .filterNot(row => row.cellIterator.asScala.mkString.trim.isEmpty)
        .map(row => {
          row.cellIterator.asScala
            .filter(cell => columnsToHeadings.contains(cell.getColumnIndex))
            .map(cell =>
              (columnsToHeadings(cell.getColumnIndex), determiner.getCellValue(cell))
            ).toMap
        })
        .filter(row => row.values.mkString.trim.nonEmpty)
        .map(row => {
          new Entity(row)
        }).toList
      ))
    }
  }
}
