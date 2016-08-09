package troglodyte.opendatr

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class DatasetTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("with some entities") {
    val entities = List(new Entity(Map()))
    val dataset = new Dataset(entities)

    describe("#getEntities") {
      it("returns the entities") {
        assert(dataset.getEntities == entities)
      }
    }
  }

  describe("#allRootAttributesUsed") {
    describe("when given no entities") {
      it("returns an empty list") {
        val dataset = new Dataset(List[Entity]())
        assert(dataset.allRootAttributesUsed.isEmpty)
      }
    }

    describe("when given entities with no values") {
      it("returns an empty list") {
        val dataset = new Dataset(List(new Entity(Map())))
        assert(dataset.allRootAttributesUsed.isEmpty)
      }
    }

    it("returns a comprehensive list of attributes") {
      forAll { (rows: List[Map[String, String]]) =>
        val dataset = new Dataset(rows.map(r => new Entity(r)))
        whenever(rows.nonEmpty) {
          val attributes = rows.map(r => r.keySet).reduce((a, b) => a.union(b))
          assert(dataset.allRootAttributesUsed.toSet == attributes)
        }
      }
    }
  }
}
