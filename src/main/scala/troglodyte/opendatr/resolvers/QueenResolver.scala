package troglodyte.opendatr.resolvers

import troglodyte.opendatr.{Announcer, Dataset}

class QueenResolver(announcer: Announcer, resolvers: List[Resolver]) extends Resolver {
  var completelyResolved = false

  override def canResolve(puzzle: Any): Boolean = {
    resolvers.exists { (resolver) =>
      resolver.canResolve(puzzle)
    }
  }

  override def resolve(puzzle: Any): Any = {
    recursiveResolve(puzzle)
  }

  def resolvedSuccessfully(): Boolean = completelyResolved

  private def recursiveResolve(puzzle: Any): Any = {
    if (canResolve(puzzle)) {
      val resolver = resolvers.find { (resolver) =>
        resolver.canResolve(puzzle)
      }.get

      announcer.announce(s"Resolving with ${resolver.getClass.getSimpleName}")
      val resolved = resolver.resolve(puzzle)
      if (resolved.isInstanceOf[Dataset]) {
        completelyResolved = true
        resolved
      } else {
        recursiveResolve(resolved)
      }
    }
  }
}
