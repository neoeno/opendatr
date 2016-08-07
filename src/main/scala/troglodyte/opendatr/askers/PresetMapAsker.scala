package troglodyte.opendatr.askers

class PresetMapAsker(answers: Map[Symbol, Boolean]) extends Asker {
  override def askYesNo(name: Symbol, question: String): Boolean = answers(name)

  override def show(information: String): Unit = {}
}
