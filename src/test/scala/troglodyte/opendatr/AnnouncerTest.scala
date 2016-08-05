package troglodyte.opendatr

import java.io.{ByteArrayOutputStream, PrintStream}

import org.scalatest.{FunSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class AnnouncerTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("#welcome") {
    it("prints its argument") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.welcome(message)
        assert(outputStream.toString.contains(message))
      }
    }

    it("prints its argument in bold cyan") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.welcome(message)
        assert(outputStream.toString.contains(Console.CYAN))
        assert(outputStream.toString.contains(Console.BOLD))
      }
    }
  }

  describe("#announce") {
    it("prints its argument") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announce(message)
        assert(outputStream.toString.contains(message))
      }
    }

    it("prints its argument in green") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announce(message)
        assert(outputStream.toString.contains(Console.CYAN))
      }
    }
  }

  describe("#announceGood") {
    it("prints its argument") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announceGood(message)
        assert(outputStream.toString.contains(message))
      }
    }

    it("prints its argument in green") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announceGood(message)
        assert(outputStream.toString.contains(Console.GREEN))
      }
    }
  }

  describe("#announceBad") {
    it("prints its argument") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announceBad(message)
        assert(outputStream.toString.contains(message))
      }
    }

    it("prints its argument in red") {
      forAll { (message: String) =>
        val outputStream = new ByteArrayOutputStream()
        val announcer = new Announcer(new PrintStream(outputStream))
        announcer.announceBad(message)
        assert(outputStream.toString.contains(Console.RED))
      }
    }
  }
}
