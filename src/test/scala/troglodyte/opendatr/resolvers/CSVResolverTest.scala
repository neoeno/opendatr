package troglodyte.opendatr.resolvers

import java.io.{File, FileWriter}

import org.scalatest.FunSpec
import troglodyte.opendatr.Dataset
import troglodyte.opendatr.askers.PresetMapAsker

class CSVResolverTest extends FunSpec {
  var resolver = new CSVResolver(new PresetMapAsker(Map()))

  describe("given a file with a comma on the first line") {
    val tempFile = File.createTempFile("temp", "file")
    val writer = new FileWriter(tempFile)
    writer.write("col1,col2\ncol1row1,col2row1\ncol1row2,col2row2")
    writer.flush()
    writer.close()

    describe("#canResolve") {
      it("is true") {
        assert(resolver.canResolve(tempFile))
      }
    }

    describe("#resolve") {
      describe("when the user signals headings") {
        var resolver = new CSVResolver(new PresetMapAsker(Map('has_headings -> true)))

        it("returns a corresponding dataset") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length === 2)
          assert(dataset.getEntities(0).getValues("col1") === "col1row1")
          assert(dataset.getEntities(0).getValues("col2") === "col2row1")
          assert(dataset.getEntities(1).getValues("col1") === "col1row2")
          assert(dataset.getEntities(1).getValues("col2") === "col2row2")
        }
      }

      describe("when the user signals no headings") {
        var resolver = new CSVResolver(new PresetMapAsker(Map('has_headings -> false)))

        it("returns a corresponding dataset") {
          val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
          assert(dataset.getEntities.length === 3)
          assert(dataset.getEntities(0).getValues("0") === "col1")
          assert(dataset.getEntities(0).getValues("1") === "col2")
          assert(dataset.getEntities(1).getValues("0") === "col1row1")
          assert(dataset.getEntities(1).getValues("1") === "col2row1")
          assert(dataset.getEntities(2).getValues("0") === "col1row2")
          assert(dataset.getEntities(2).getValues("1") === "col2row2")
        }
      }
    }
  }

  describe("given a weirder CSV file") {
    var resolver = new CSVResolver(new PresetMapAsker(Map('has_headings -> true)))
    val tempFile = File.createTempFile("temp", "file")
    val writer = new FileWriter(tempFile)
    writer.write("col 1, col 2\n")
    writer.write("row 1 column 1, row 1 column 2\n")
    writer.write("\"row 2, column 1 \",row 2 \"\"column' 2\n")
    writer.flush()
    writer.close()

    describe("#resolve") {
      it("returns a corresponding dataset") {
        val dataset = resolver.resolve(tempFile).get.asInstanceOf[Dataset]
        assert(dataset.getEntities.length === 2)
        assert(dataset.getEntities(0).getValues("col 1") === "row 1 column 1")
        assert(dataset.getEntities(0).getValues(" col 2") === " row 1 column 2")
        assert(dataset.getEntities(1).getValues("col 1") === "row 2, column 1 ")
        assert(dataset.getEntities(1).getValues(" col 2") === "row 2 \"column' 2")
      }
    }
  }

  describe("given a file with no comma on the first line") {
    val tempFile = File.createTempFile("temp", "file")
    val writer = new FileWriter(tempFile)
    writer.write("hello world")
    writer.flush()
    writer.close()

    describe("#canResolve") {
      it("is false") {
        assert(resolver.canResolve(tempFile) === false)
      }
    }
  }

  describe("given a random object") {
    describe("#canResolve") {
      it("is false") {
        assert(resolver.canResolve(new Object) === false)
      }
    }
  }
}
