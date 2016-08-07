package troglodyte.opendatr.exporters

import troglodyte.opendatr.Dataset

trait Exporter {
  def export(dataset: Dataset): Unit
}
