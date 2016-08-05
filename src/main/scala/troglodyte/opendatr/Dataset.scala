package troglodyte.opendatr

class Dataset {
  private var entities = List[Entity]()
  def add(entity: Entity) = entities = entity :: entities
  def getEntities(): List[Entity] = entities
}
