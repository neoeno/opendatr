package troglodyte.opendatr.resolvers

import java.io.{Closeable, File, FileReader}

import troglodyte.opendatr.{Dataset, Entity}

import scala.io.Source

class CSVResolver extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case file: File =>
      withLines(file) { (lines: Iterator[String]) =>
        lines.hasNext && lines.next.contains(",")
      }
    case _ => false
  }

  override def resolve(puzzle: Any): Any = {
    val file = puzzle.asInstanceOf[File]
    withLines(file) { (lines: Iterator[String]) =>
      val attributeLabels = lines.next.split(",")
      new Dataset(lines.map { (line: String) =>
        new Entity(attributeLabels.zip(line.split(",")).toMap)
      }.toList)
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
