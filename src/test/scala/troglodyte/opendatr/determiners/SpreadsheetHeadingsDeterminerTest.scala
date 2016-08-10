package troglodyte.opendatr.determiners

import org.scalatest.FunSpec
import troglodyte.opendatr.TestFactory
import troglodyte.opendatr.askers.{Asker, PresetMapAsker}

class SpreadsheetHeadingsDeterminerTest extends FunSpec {
  describe("#determineHeadings") {
    describe("given a list of full rows") {
      val rows = List(
        List("col1", "col2", "col3"),
        List(4, 5, 6),
        List(7, 8, 9)
      ).zipWithIndex

      it("asks the user to pick and follows the user's answer") {
        val determiner = new SpreadsheetHeadingsDeterminer(new PresetMapAsker(Map('pick_headings_row -> Some(1))))
        assert(determiner.determineHeadings(rows).contains(1))
      }
    }

    describe("given a list of rows some of which are empty") {
      val rows = List(
        List(),
        List("", " ", ""),
        List("col1", "col2", "col3"),
        List(4, 5, 6)
      ).zipWithIndex

      it("does not present the user with empty rows") {
        val determiner = new SpreadsheetHeadingsDeterminer(new TestFactory.UnimplementedAsker {
          override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = {
            assert(options == List(
              "col1, col2, col3",
              "4, 5, 6"
            ))
            Some(0)
          }
        })

        assert(determiner.determineHeadings(rows).contains(2))
      }
    }

    describe("given a list of rows some of which have gaps") {
      val rows = List(
        List("hello", " ", "world"),
        List("col1", "col2", "col3"),
        List(4, 5, 6)
      ).zipWithIndex

      it("does not present the user with the gappy rows") {
        val determiner = new SpreadsheetHeadingsDeterminer(new TestFactory.UnimplementedAsker {
          override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = {
            assert(options == List(
              "col1, col2, col3",
              "4, 5, 6"
            ))
            Some(0)
          }
        })

        assert(determiner.determineHeadings(rows).contains(1))
      }
    }

    describe("given a list of rows some of which have blank ends") {
      val rows = List(
        List("", "col1", "col2", "col3", "", ""),
        List(4, 5, 6)
      ).zipWithIndex

      it("does not present the user with the blank ends") {
        val determiner = new SpreadsheetHeadingsDeterminer(new TestFactory.UnimplementedAsker {
          override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = {
            assert(options == List(
              "col1, col2, col3",
              "4, 5, 6"
            ))
            Some(0)
          }
        })

        assert(determiner.determineHeadings(rows).contains(0))
      }
    }


    describe("given a list of rows of differing lengths") {
      val rows = List(
        List("overall_title"),
        List("col1", "col2", "col3"),
        List(4, 5, 6)
      ).zipWithIndex

      it("orders choices by closeness to the median length") {
        val determiner = new SpreadsheetHeadingsDeterminer(new TestFactory.UnimplementedAsker {
          override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = {
            assert(options == List(
              "col1, col2, col3",
              "4, 5, 6",
              "overall_title"
            ))
            Some(0)
          }
        })

        assert(determiner.determineHeadings(rows).contains(1))
      }
    }
  }
}
