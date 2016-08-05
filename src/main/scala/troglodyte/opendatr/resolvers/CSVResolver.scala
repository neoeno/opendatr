package troglodyte.opendatr.resolvers

import java.io.{Closeable, File, FileReader}

import scala.io.Source

class CSVResolver extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case file: File =>
      withLines(file) { (lines: Iterator[String]) =>
        lines.hasNext && lines.next.contains(",")
      }
    case _ => false
  }

  override def resolve(puzzle: Any): Any = ???

  def withLines[T](file: File)(f: Iterator[String] => T): T = {
    val source = Source.fromFile(file)
    val lines = source.getLines
    val returnValue = f(lines)
    source.close
    returnValue
  }
}
