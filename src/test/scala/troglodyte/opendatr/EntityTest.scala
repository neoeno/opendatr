package troglodyte.opendatr

import org.scalatest.FunSpec

class EntityTest extends FunSpec {
  describe("with some entities") {
    val values = Map[String, Object]()
    val entity = new Entity(values)

    describe("#getEntities") {
      it("returns the entities") {
        assert(entity.getValues == values)
      }
    }
  }
}
