package troglodyte.opendatr.resolvers

import java.io.{File, PrintStream}

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import troglodyte.opendatr.{Announcer, TestFactory}

class QueenResolverTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("with empty list") {
    describe("#canResolve") {
      it("can't actually resolve anything yet") {
        val announcerPair = TestFactory.makeAnnouncer()
        val resolver = new QueenResolver(announcerPair._1, List())
        forAll { (anything: String) =>
          assert(resolver.canResolve(anything) === false)
        }
      }
    }

    describe("#resolve") {
      it("reports its disappointment") {
        forAll { (anything: String) =>
          val announcerPair = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcerPair._1, List())
          resolver.resolve(anything)
          assert(announcerPair._2.toString.contains("Sorry, couldn't resolve this one! You're on your own!"))
        }
      }
    }
  }

  describe("with a singleton list of PathResolver") {
    val announcerPair = TestFactory.makeAnnouncer()
    val resolver = new QueenResolver(announcerPair._1, List(new PathResolver))

    describe("and a valid path") {
      val tempFile = File.createTempFile("temp", "file")

      describe("#canResolve") {
        it("is true") {
          assert(resolver.canResolve(tempFile.getAbsolutePath))
        }
      }

      describe("#resolve") {
        it("reports that it is resolving using PathResolver") {
          resolver.resolve(tempFile.getAbsolutePath)
          assert(announcerPair._2.toString.contains("Resolving with PathResolver"))
        }

        it("reports that it cannot resolve any further") {
          resolver.resolve(tempFile.getAbsolutePath)
          assert(announcerPair._2.toString.contains("Sorry, couldn't resolve this one! You're on your own!"))
        }
      }
    }

    describe("and an invalid path") {
      describe("#canResolve") {
        it("is false") {
          assert(resolver.canResolve("non/extant/path") === false)
        }
      }
    }

    describe("and a random object") {
      describe("#canResolve") {
        it("is false") {
          assert(resolver.canResolve(new Object) === false)
        }
      }
    }
  }
}
