package troglodyte.opendatr

import org.scalatest.FunSpec

class DatasetTest extends FunSpec {
  val dataset = new Dataset

  describe("after adding an entity") {
    val entity = new Entity(Map())
    dataset.add(entity)

    it("contains a singleton list of that entity") {
      assert(dataset.getEntities().length === 1)
      assert(dataset.getEntities()(0) === entity)
    }
  }
}
