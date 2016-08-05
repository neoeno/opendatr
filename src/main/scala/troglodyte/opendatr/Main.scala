package troglodyte.opendatr

import troglodyte.opendatr.resolvers.{PathResolver, QueenResolver}

object Main extends App {
  val announcer = new Announcer(System.out)
  validateArgs()

  announcer.welcome("Welcome to OpenDatr!")

  val initialPuzzle = getInitialPuzzleFromArgs

  new QueenResolver(announcer, List(
    new PathResolver
  )).resolve(initialPuzzle)

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
