package troglodyte.opendatr

import java.io.{ByteArrayOutputStream, PrintStream}

object TestFactory {
  def makeAnnouncer(): (Announcer, ByteArrayOutputStream) = {
    val outputStream = new ByteArrayOutputStream()
    (new Announcer(new PrintStream(outputStream)), outputStream)
  }
}
