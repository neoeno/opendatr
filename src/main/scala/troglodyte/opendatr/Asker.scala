package troglodyte.opendatr

import pl.project13.scala.rainbow.Rainbow._

import scala.io.StdIn

object Asker {
  def askYesNo(question: String): Boolean = {
    print(bullet(question.yellow))
    val answer = StdIn.readLine()
    !answer.matches("[nN]")
  }

  private def bullet(message: String) = "> ".white.concat(message)
}
