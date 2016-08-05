package troglodyte.opendatr

import org.scalatest.FunSpec

class DatasetTest extends FunSpec {
  describe("with some entities") {
    val entities = List(new Entity(Map()))
    val dataset = new Dataset(entities)

    describe("#getEntities") {
      it("returns the entities") {
        assert(dataset.getEntities == entities)
      }
    }
  }
}
