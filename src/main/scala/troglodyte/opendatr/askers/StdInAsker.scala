package troglodyte.opendatr.askers

import pl.project13.scala.rainbow.Rainbow._

import scala.io.StdIn
import scala.Function.tupled

class StdInAsker extends Asker {
  override def askYesNo(name: Symbol, question: String): Boolean = {
    print(bullet(s"$question [Yn] ".yellow))
    val answer = StdIn.readLine()
    !answer.matches("[nN]")
  }

  override def show(information: String): Unit = println(information)

  override def choose(name: Symbol, question: String, options: List[String]): Option[Integer] = {
    options.zipWithIndex.foreach(tupled { (option, idx) =>
      println(s"${idx+1}. $option".white)
    })
    print(bullet(s"question [1-${options.length} or N if none] "))
    val answer = StdIn.readLine()
    if (answer.matches("[nN]")) {
      None
    } else {
      Some(answer.toInt-1)
    }
  }

  private def bullet(message: String) = "> ".white.concat(message)
}
