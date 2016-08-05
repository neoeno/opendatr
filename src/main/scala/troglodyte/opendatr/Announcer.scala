package troglodyte.opendatr

import java.io.PrintStream

import pl.project13.scala.rainbow.Rainbow._

class Announcer(stream: PrintStream) {
  def getStream(): PrintStream = stream

  def welcome(message: String) = stream.println(message.cyan.bold + "\n")
  def announce(message: String) = stream.println(bullet(message.cyan))
  def announceBad(message: String) = stream.println(bullet(message.red))
  def announceGood(message: String) = stream.println(bullet(message.green))

  private def bullet(message: String) = "• ".white.concat(message)
}
