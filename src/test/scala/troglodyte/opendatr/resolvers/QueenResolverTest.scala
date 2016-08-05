package troglodyte.opendatr.resolvers

import java.io.PrintStream

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import troglodyte.opendatr.{Announcer, TestFactory}

class QueenResolverTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("with empty list") {
    describe("#canResolve") {
      it("can't actually resolve anything yet") {
        val announcerPair = TestFactory.makeAnnouncer()
        val queenResolver = new QueenResolver(announcerPair._1, List())
        forAll { (anything: String) =>
          assert(queenResolver.canResolve(anything) === false)
        }
      }
    }

    describe("#resolve") {
      it("reports its disappointment") {
        forAll { (anything: String) =>
          val announcerPair = TestFactory.makeAnnouncer()
          val queenResolver = new QueenResolver(announcerPair._1, List())
          queenResolver.resolve(anything)
          assert(announcerPair._2.toString.contains("Sorry, couldn't resolve this one! You're on your own!"))
        }
      }
    }
  }
}
