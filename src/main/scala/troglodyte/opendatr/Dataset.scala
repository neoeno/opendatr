package troglodyte.opendatr

class Dataset(attributes: List[String], entities: List[Entity]) {
  def getEntities = entities
  def getAttributes = attributes
}
