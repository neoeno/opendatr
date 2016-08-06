package troglodyte.opendatr.resolvers

import java.io.{Closeable, File, FileReader}

import com.github.tototoshi.csv.CSVReader
import troglodyte.opendatr.{Asker, Dataset, Entity}

import scala.collection
import scala.io.Source

class CSVResolver extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case file: File =>
      withLines(file) { (lines: Iterator[String]) =>
        lines.hasNext && lines.next.contains(",")
      }
    case _ => false
  }

  override def resolve(puzzle: Any): Option[Any] = {
    val file = puzzle.asInstanceOf[File]
    val reader = CSVReader.open(file)
    println(reader.iterator.take(1).toList(0).mkString(", "))
    if (Asker.askYesNo(s"Do these look like column headings to you? [Yn] ")) {
      Some(
        new Dataset(reader.iteratorWithHeaders.map { (row: Map[String, String]) =>
          new Entity(row)
        }.toList)
      )
    } else {
      Some(new Dataset(reader.iterator.map { (row: Seq[String]) =>
        // should these keys really be strings?
        new Entity(
          row.zipWithIndex.map(pair => (pair._2.toString, pair._1)).toMap
        )
      }.toList))
    }
  }

  def withLines[T](file: File)(f: Iterator[String] => T): T = {
    val source = Source.fromFile(file)
    val lines = source.getLines
    val returnValue = f(lines)
    source.close
    returnValue
  }
}
