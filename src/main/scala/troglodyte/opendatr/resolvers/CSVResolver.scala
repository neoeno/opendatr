package troglodyte.opendatr.resolvers

import java.io.File

import com.github.tototoshi.csv.CSVReader
import troglodyte.opendatr.askers.Asker
import troglodyte.opendatr.utils.ResolverUtils
import troglodyte.opendatr.{Dataset, Entity}

import scala.Function.tupled
import scala.io.Source

class CSVResolver(asker: Asker) extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case file: File =>
      try {
        withLines(file) { (lines: Iterator[String]) =>
          lines.hasNext && lines.next.contains(",")
        }
      } catch {
        case _: Exception => false
      }
    case _ => false
  }

  override def resolve(puzzle: Any): Option[Any] = {
    val file = puzzle.asInstanceOf[File]
    val (ignoreRow, columnsToHeadings) = getColumnsToHeadingsMap(file)
    ResolverUtils.withClosing(CSVReader.open(file)) { reader =>
      Some(new Dataset(
        reader.iterator.zipWithIndex
          .filterNot(tupled { (_, idx) =>
            ignoreRow.contains(idx)
          }).map(tupled { (row, _) =>
            new Entity(row.zipWithIndex.map(tupled { (cell, idx) =>
              (columnsToHeadings(idx), cell)
            }).toMap)
          }).toList
      ))
    }
  }

  private def getColumnsToHeadingsMap(file: File): (Option[Int], Map[Int, String]) = {
    ResolverUtils.withClosing(CSVReader.open(file)) { reader =>
      val firstRow = reader.iterator.take(1).toList.head
      asker.show(firstRow.mkString(", "))
      if (asker.askYesNo('has_headings, "Do these look like column headings to you?")) {
        (Some(0), firstRow.indices.zip(firstRow).toMap)
      } else {
        (None, firstRow.indices.zip(firstRow.indices.map(n => n.toString)).toMap)
      }
    }
  }

  private def hasHeadings(file: File): Boolean = {
    val headingReader = CSVReader.open(file)
    asker.show(headingReader.iterator.take(1).toList.head.mkString(", "))
    headingReader.close()
    asker.askYesNo('has_headings, "Do these look like column headings to you?")
  }

  def withLines[T](file: File)(f: Iterator[String] => T): T = {
    val source = Source.fromFile(file)
    val lines = source.getLines
    val returnValue = f(lines)
    source.close
    returnValue
  }
}
