package troglodyte.opendatr.askers

trait Asker {
  def show(information: String): Unit
  def askYesNo(name: Symbol, question: String): Boolean
}
