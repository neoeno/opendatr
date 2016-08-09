package troglodyte.opendatr.exporters

import java.io.StringWriter

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import troglodyte.opendatr.{Dataset, Entity}

class CSVExporterTest extends FunSpec with GeneratorDrivenPropertyChecks {
  describe("#export") {
    describe("given an empty dataset") {
      val dataset = new Dataset(List())
      it("writes a blank file") {
        val writer = new StringWriter()
        val exporter = new CSVExporter(writer)
        exporter.export(dataset)
        assert(writer.toString.matches("^\\s*$"))
      }
    }

    describe("given a dataset") {
      it("writes it as CSV") {
        val writer = new StringWriter()
        val exporter = new CSVExporter(writer)
        val dataset = new Dataset(List(new Entity(Map("hello" -> "world", "sup" -> "kid")), new Entity(Map("hello" -> "dude", "sup" -> "bro"))))
        exporter.export(dataset)
        val lines = writer.toString.trim.split("\r\n").toList
        assert(lines == List("hello,sup", "world,kid", "dude,bro"))
      }
    }

    describe("given a dataset with uneven attributes") {
      it("writes it as CSV") {
        val writer = new StringWriter()
        val exporter = new CSVExporter(writer)
        val dataset = new Dataset(List(new Entity(Map("hello" -> "world")), new Entity(Map("sup" -> "bro"))))
        exporter.export(dataset)
        val lines = writer.toString.trim.split("\r\n").toList
        assert(lines == List("hello,sup", "world,", ",bro"))
      }
    }
  }
}
