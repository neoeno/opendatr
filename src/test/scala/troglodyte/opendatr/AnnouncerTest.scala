package troglodyte.opendatr

import java.io.{ByteArrayOutputStream, PrintStream}

import org.scalatest.{FunSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class AnnouncerTest extends FunSpec with GeneratorDrivenPropertyChecks {

  describe("Announcer") {
    it("prints its argument") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announce(message)
        assert(outputStream.toString.contains(message))
      }
    }
  }
}
