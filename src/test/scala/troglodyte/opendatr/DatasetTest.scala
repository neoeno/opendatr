package troglodyte.opendatr

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DatasetTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("with some entities") {
    val entities = List(new Entity(Map("hello" -> "world")))
    val dataset = new Dataset(List(), entities)

    describe("#getEntities") {
      it("returns the entities") {
        assert(dataset.getEntities == entities)
      }
    }
  }

  describe("with some attributes") {
    val attributes = List("hello")
    val dataset = new Dataset(attributes, List(new Entity(Map())))

    describe("#getAttributes") {
      it("returns the attributes") {
        assert(dataset.getAttributes == attributes)
      }
    }
  }
}
