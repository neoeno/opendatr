package troglodyte.opendatr

class Dataset(entities: List[Entity]) {
  def getEntities = entities

  def getAttributes: List[String] = {
    if (entities.isEmpty) { return List() }
    // We do this here by iterating through all Entities and
    // unioning up all the keys. Mostly preserves order, probably quite costly
    entities.foldLeft(Set[String]()) { (acc, entity) =>
      acc.union(entity.getValues.keySet)
    }.toList
  }
}
