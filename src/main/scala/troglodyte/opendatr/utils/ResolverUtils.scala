package troglodyte.opendatr.utils

import java.io.Closeable

object ResolverUtils {
  def withClosing[T, R <: Closeable](reader: R)(f: R => T): T = {
    val retVal = f(reader)
    reader.close()
    retVal
  }
}
