package troglodyte.opendatr.exporters

import java.io.StringWriter

import org.scalatest.FunSpec
import troglodyte.opendatr.{Dataset, Entity}

class JSONExporterTest extends FunSpec {
  describe("#export") {
    describe("given an empty dataset") {
      val dataset = new Dataset(List())
      it("writes an empty JSON array") {
        val writer = new StringWriter()
        val exporter = new JSONExporter(writer)
        exporter.export(dataset)
        assert(writer.toString == "[]")
      }
    }

    describe("given a dataset") {
      it("writes it as JSON") {
        val writer = new StringWriter()
        val exporter = new JSONExporter(writer)
        val dataset = new Dataset(List(new Entity(Map("hello" -> "world", "sup" -> "kid")), new Entity(Map("hello" -> "dude", "sup" -> "bro"))))
        exporter.export(dataset)
        assert(writer.toString == "[{\"hello\":\"world\",\"sup\":\"kid\"},{\"hello\":\"dude\",\"sup\":\"bro\"}]")
      }
    }

    describe("given a dataset with uneven attributes") {
      it("writes it as JSON") {
        val writer = new StringWriter()
        val exporter = new JSONExporter(writer)
        val dataset = new Dataset(List(new Entity(Map("hello" -> "world")), new Entity(Map("sup" -> "bro"))))
        exporter.export(dataset)
        assert(writer.toString == "[{\"hello\":\"world\"},{\"sup\":\"bro\"}]")
      }
    }

    describe("given a dataset with weird types") {
      it("throws an exception") {
        val writer = new StringWriter()
        val exporter = new JSONExporter(writer)
        val dataset = new Dataset(List(new Entity(Map("hello" -> new Object()))))
        val caught = intercept[IllegalArgumentException] {
          exporter.export(dataset)
        }
        assert(caught.getMessage == "Don't know how to export attribute 'hello' of type class java.lang.Object")
      }
    }
  }
}
