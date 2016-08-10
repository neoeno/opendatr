package troglodyte.opendatr

import java.io.{ByteArrayOutputStream, PrintStream}

import troglodyte.opendatr.askers.Asker

object TestFactory {
  def makeAnnouncer(): (Announcer) = {
    val outputStream = new ByteArrayOutputStream()
    new Announcer(new PrintStream(outputStream))
  }

  case class UnimplementedAsker() extends Asker {
    override def show(information: String): Unit = ???
    override def askYesNo(name: Symbol, question: String): Boolean = ???
    override def choose(name: Symbol, question: String, options: List[String]): Integer = ???
    override def chooseOrRefuse(name: Symbol, question: String, options: List[String]): Option[Integer] = ???
  }
}
