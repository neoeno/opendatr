package troglodyte.opendatr.askers

import pl.project13.scala.rainbow.Rainbow._

import scala.io.StdIn

class StdInAsker extends Asker {
  override def askYesNo(name: Symbol, question: String): Boolean = {
    print(bullet(s"$question [Yn] ".yellow))
    val answer = StdIn.readLine()
    !answer.matches("[nN]")
  }

  override def show(information: String): Unit = println(information)

  private def bullet(message: String) = "> ".white.concat(message)
}
