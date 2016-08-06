package troglodyte.opendatr.resolvers

import java.io.File

class PathResolver extends Resolver {
  override def canResolve(puzzle: Any): Boolean = puzzle match {
    case path: String =>
      new File(path).exists()
    case _ =>
      false
  }

  override def resolve(puzzle: Any): Option[Any] = {
    val path = puzzle.asInstanceOf[String]
    Option(new File(path))
  }
}
