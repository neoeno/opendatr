package troglodyte.opendatr.resolvers

import troglodyte.opendatr.Announcer

class QueenResolver(announcer: Announcer, resolvers: List[Resolver]) extends Resolver {
  override def canResolve(puzzle: Any): Boolean = {
    resolvers.exists { (resolver) =>
      resolver.canResolve(puzzle)
    }
  }

  override def resolve(puzzle: Any): Any = {
    recursiveResolve(puzzle)
  }

  private def recursiveResolve(puzzle: Any): Any = {
    if (canResolve(puzzle)) {
      val resolver = resolvers.find { (resolver) =>
        resolver.canResolve(puzzle)
      }.get

      announcer.announceGood(s"Resolving with ${resolver.getClass.getSimpleName}")
      resolve(resolver.resolve(puzzle))
    } else {
      announcer.announceBad("Sorry, couldn't resolve this one! You're on your own!")
    }
  }
}
