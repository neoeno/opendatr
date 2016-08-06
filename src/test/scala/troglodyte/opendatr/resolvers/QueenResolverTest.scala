package troglodyte.opendatr.resolvers

import java.io.{File, PrintStream}

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import troglodyte.opendatr.{Announcer, TestFactory}

class QueenResolverTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("with empty list") {
    describe("#canResolve") {
      it("can't actually resolve anything yet") {
        val announcer = TestFactory.makeAnnouncer()
        val resolver = new QueenResolver(announcer, List())
        forAll { (anything: String) =>
          assert(resolver.canResolve(anything) === false)
        }
      }
    }

    describe("#resolve") {
      it("is empty") {
        forAll { (anything: String) =>
          val announcer = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcer, List())
          assert(resolver.resolve(anything).isEmpty)
        }
      }
    }
  }

  describe("with a singleton list of PathResolver") {
    describe("and a valid path") {
      val tempFile = File.createTempFile("temp", "file")

      describe("#canResolve") {
        it("is true") {
          val announcer = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcer, List(new PathResolver))
          assert(resolver.canResolve(tempFile.getAbsolutePath))
        }
      }

      describe("#resolve") {
        it("cannot resolve completely, so is empty") {
          val announcer = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcer, List(new PathResolver))
          assert(resolver.resolve(tempFile.getAbsolutePath).isEmpty)
        }
      }
    }

    describe("and an invalid path") {
      describe("#canResolve") {
        it("is false") {
          val announcer = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcer, List(new PathResolver))
          assert(resolver.canResolve("non/extant/path") === false)
        }
      }
    }

    describe("and a random object") {
      describe("#canResolve") {
        it("is false") {
          val announcer = TestFactory.makeAnnouncer()
          val resolver = new QueenResolver(announcer, List(new PathResolver))
          assert(resolver.canResolve(new Object) === false)
        }
      }
    }
  }
}
