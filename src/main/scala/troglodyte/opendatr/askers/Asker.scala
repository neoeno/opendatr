package troglodyte.opendatr.askers

trait Asker {
  def show(information: String): Unit
  def askYesNo(name: Symbol, question: String): Boolean
  def choose(name: Symbol, question: String, options: List[String]): Option[Integer]
}
