package troglodyte.opendatr.exporters
import java.io.Writer

import com.github.tototoshi.csv.CSVWriter
import troglodyte.opendatr.Dataset

class CSVExporter(fileWriter: Writer) extends Exporter {
  override def export(dataset: Dataset): Unit = {
    val writer = CSVWriter.open(fileWriter)
    val attributes = dataset.getAttributes
    writer.writeRow(attributes)
    dataset.getEntities.foreach {
      entity => writer.writeRow(attributes.map {
        attribute => entity.getValues.getOrElse(attribute, null)
      })
    }
    writer.close()
  }
}
