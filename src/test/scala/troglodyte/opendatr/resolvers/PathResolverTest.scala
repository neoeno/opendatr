package troglodyte.opendatr.resolvers

import java.io.File

import org.scalatest.FunSpec

class PathResolverTest extends FunSpec {
  val resolver = new PathResolver()

  describe("when input is an extant file") {
    val tempFile = File.createTempFile("temp", "file")

    describe("#canResolve") {
      it("is true") {
        assert(resolver.canResolve(tempFile.getAbsolutePath) === true)
      }
    }

    describe("#resolve") {
      it("resolves to a File") {
        val resolved = resolver.resolve(tempFile.getAbsolutePath)
        assert(resolved.get.isInstanceOf[File])
        assert(resolved.get.asInstanceOf[File].getAbsolutePath == tempFile.getAbsolutePath)
      }
    }
  }

  describe("when input is not an extant file") {
    describe("#canResolve") {
      it("is false") {
        assert(resolver.canResolve("non/extant/path") === false)
      }
    }
  }

  describe("when input is a random object") {
    describe("#canResolve") {
      it("is false") {
        assert(resolver.canResolve(new Object) === false)
      }
    }
  }
}
