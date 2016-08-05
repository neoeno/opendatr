package troglodyte.opendatr.resolvers

import java.io.{File, FileWriter}

import org.scalatest.FunSpec
import troglodyte.opendatr.Dataset

class CSVResolverTest extends FunSpec {
  val resolver = new CSVResolver

  describe("given a file with a comma on the first line") {
    val tempFile = File.createTempFile("temp", "file")
    val writer = new FileWriter(tempFile)
    writer.write("hello,world\nthe,thing")
    writer.flush()
    writer.close()

    describe("#canResolve") {
      it("is true") {
        assert(resolver.canResolve(tempFile))
      }
    }

    describe("#resolve") {
      it("returns a corresponding dataset") {
        val dataset = resolver.resolve(tempFile).asInstanceOf[Dataset]
        assert(dataset.getEntities.length === 1)
        assert(dataset.getEntities.head.getValues("hello") === "the")
        assert(dataset.getEntities.head.getValues("world") === "thing")
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
