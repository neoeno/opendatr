package troglodyte.opendatr

import java.io.PrintStream

import pl.project13.scala.rainbow.Rainbow._

class Announcer(stream: PrintStream) {
  def getStream(): PrintStream = stream
  def announceBad(message: String) = stream.println(message.red)
  def announceGood(message: String) = stream.println(message.green)
}
