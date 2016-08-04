package troglodyte.opendatr

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class MainTest extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {

  describe("Main") {
    it("works") {
      forAll { (str: String) =>
        val main = new Main()
        main.main(Array(str)) should equal()
      }
    }
  }

}
