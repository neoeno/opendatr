package troglodyte.opendatr.resolvers

import troglodyte.opendatr.Announcer

class QueenResolver(announcer: Announcer) extends Resolver {
  private val resolvers = List[Resolver](
  )

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
      resolvers.find { (resolver) =>
        resolver.canResolve(puzzle)
      }.get.resolve(puzzle)
    } else {
      announcer.announceBad("Sorry, couldn't resolve this one! You're on your own!")
    }
  }
}
