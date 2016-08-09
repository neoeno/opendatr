package troglodyte.opendatr.resolvers

import java.io.{Closeable, File}

import com.github.tototoshi.csv.CSVReader
import troglodyte.opendatr.askers.Asker
import troglodyte.opendatr.{Dataset, Entity}

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
    withClosing(CSVReader.open(file)) { (reader: CSVReader) =>
      Some(new Dataset(
        if (hasHeadings(file)) {
          reader.iteratorWithHeaders.map { (row: Map[String, String]) =>
            new Entity(row)
          }.toList
        } else {
          reader.iterator.map { (row: Seq[String]) =>
            // should these keys really be strings?
            new Entity(
              row.zipWithIndex.map(pair => (pair._2.toString, pair._1)).toMap
            )
          }.toList
        }
      ))
    }
  }

  private def hasHeadings(file: File): Boolean = {
    val headingReader = CSVReader.open(file)
    asker.show(headingReader.iterator.take(1).toList.head.mkString(", "))
    headingReader.close()
    asker.askYesNo('has_headings, "Do these look like column headings to you?")
  }

  private def withClosing[T, R <: Closeable](reader: R)(f: R => T): T = {
    val retVal = f(reader)
    reader.close()
    retVal
  }

  def withLines[T](file: File)(f: Iterator[String] => T): T = {
    val source = Source.fromFile(file)
    val lines = source.getLines
    val returnValue = f(lines)
    source.close
    returnValue
  }
}
