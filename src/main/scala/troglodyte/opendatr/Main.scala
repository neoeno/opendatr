package troglodyte.opendatr

import java.io.FileWriter

import troglodyte.opendatr.askers.StdInAsker
import troglodyte.opendatr.exporters.JSONExporter
import troglodyte.opendatr.resolvers.{CSVResolver, ExcelResolver, PathResolver, QueenResolver}

object Main extends App {
  val announcer = new Announcer(System.out)
  val asker = new StdInAsker()
  validateArgs()

  announcer.welcome("Welcome to OpenDatr!")

  val initialPuzzle = getInitialPuzzleFromArgs

  val resolver = new QueenResolver(announcer, List(
    new PathResolver,
    new CSVResolver(asker),
    new ExcelResolver(asker)
  ))

  val resolved = resolver.resolve(initialPuzzle).getOrElse {
    announcer.announceBad("Sorry, couldn't resolve this one! You're on your own!")
  }.asInstanceOf[Dataset]

  announcer.announceGood("Successfully resolved!")

  new JSONExporter(new FileWriter("output.json")).export(resolved)

  def getInitialPuzzleFromArgs: String = {
    args(0)
  }

  def validateArgs(): Unit = {
    if (args.length != 1) {
      announcer.announceGood("Usage: sbt run <URL or path to file>")
      System.exit(1)
    }
  }
}
