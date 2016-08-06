package troglodyte.opendatr.resolvers

import troglodyte.opendatr.{Announcer, Dataset}

class QueenResolver(announcer: Announcer, resolvers: List[Resolver]) extends Resolver {
  override def canResolve(puzzle: Any): Boolean = {
    resolvers.exists { (resolver) =>
      resolver.canResolve(puzzle)
    }
  }

  // We resolve recursively by continually trying to resolve the resolutions
  override def resolve(puzzle: Any): Option[Any] = {
    resolvers.find { (resolver) =>
      // First we see if any of our resolvers claim to be able to resolve our puzzle
      resolver.canResolve(puzzle)
    }.flatMap { (resolver: Resolver) =>
      // Then we do a resolve, and get an option back
      announcer.announce(s"Resolving with ${resolver.getClass.getSimpleName}")
      resolver.resolve(puzzle)
    }.flatMap { (resolved: Any) =>
      // Then, if the option is pregnant, we check if it's a dataset (our terminal value)
      if (resolved.isInstanceOf[Dataset]) {
        // Returning it if so
        Some(resolved)
      } else {
        // Or recursing if not
        resolve(resolved)
      }
    }
  }
}
