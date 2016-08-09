package troglodyte.opendatr.resolvers

import troglodyte.opendatr.Dataset

trait DatasetGeneratingResolver extends Resolver {
  override def resolve(puzzle: Any): Option[Dataset]
}
