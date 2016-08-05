package troglodyte.opendatr.resolvers

trait Resolver {
  def canResolve(puzzle: Any): Boolean
  def resolve(puzzle: Any): Any
}
