package troglodyte.opendatr

import troglodyte.opendatr.resolvers.QueenResolver

object Main extends App {
  val announcer = new Announcer(System.out)
  validateArgs()

  announcer.announceGood("Welcome to OpenDatr!")

  val initialPuzzle = getInitialPuzzleFromArgs

  new QueenResolver(announcer).resolve(initialPuzzle)

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
