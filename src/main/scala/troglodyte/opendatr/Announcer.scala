package troglodyte.opendatr

import java.io.PrintStream

import pl.project13.scala.rainbow.Rainbow._

class Announcer(stream: PrintStream) {
  def announce(message: String) = stream.println(message.green)
}
