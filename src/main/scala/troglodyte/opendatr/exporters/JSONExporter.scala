package troglodyte.opendatr.exporters
import java.io.Writer

import troglodyte.opendatr.Dataset
import com.google.gson.stream.JsonWriter

class JSONExporter(fileWriter: Writer) extends Exporter {
  override def export(dataset: Dataset): Unit = {
    val writer = new JsonWriter(fileWriter)
    writer.beginArray()
    dataset.getEntities.foreach(entity => {
      writer.beginObject()
      entity.getValues.foreach(pair => {
        pair._2 match {
          case value: String => writer.name(pair._1).value(value)
          case value: Number => writer.name(pair._1).value(value)
          case value: java.util.Date => writer.name(pair._1).value(value.toString)
          case _ => throw new IllegalArgumentException(s"Don't know how to export attribute '${pair._1}' of type ${pair._2.getClass}")
        }
      })
      writer.endObject()
    })
    writer.endArray()
    writer.close()
  }
}
