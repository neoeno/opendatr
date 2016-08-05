package troglodyte.opendatr

import java.io.{ByteArrayOutputStream, PrintStream}

object TestFactory {
  def makeAnnouncer(): (Announcer) = {
    val outputStream = new ByteArrayOutputStream()
    new Announcer(new PrintStream(outputStream))
  }
}
