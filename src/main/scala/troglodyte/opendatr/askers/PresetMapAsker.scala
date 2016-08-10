package troglodyte.opendatr.askers

class PresetMapAsker(answers: Map[Symbol, Any]) extends Asker {
  override def askYesNo(name: Symbol, question: String): Boolean = answers(name) match {
    case answer: Boolean => answer
    case _ => throw new IllegalArgumentException(s"Cannot give yes/no answer to $name")
  }

  override def show(information: String): Unit = {}

  override def choose(name: Symbol, question: String, options: List[String]): Integer = answers(name) match {
    case answer: Integer => answer
    case _ => throw new IllegalArgumentException(s"Cannot give numeric answer to $name")
  }

  override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = answers(name) match {
    case answer: Option[Integer] => answer
    case _ => throw new IllegalArgumentException(s"Cannot give optional numeric answer to $name")
  }
}
