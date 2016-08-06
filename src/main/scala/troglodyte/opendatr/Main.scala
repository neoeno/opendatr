package troglodyte.opendatr

import troglodyte.opendatr.resolvers.{CSVResolver, PathResolver, QueenResolver}

object Main extends App {
  val announcer = new Announcer(System.out)
  validateArgs()

  announcer.welcome("Welcome to OpenDatr!")

  val initialPuzzle = getInitialPuzzleFromArgs

  val resolver = new QueenResolver(announcer, List(
    new PathResolver,
    new CSVResolver
  ))

  val resolved = resolver.resolve(initialPuzzle).getOrElse {
    announcer.announceBad("Sorry, couldn't resolve this one! You're on your own!")
  }

  announcer.announceGood("Successfully resolved!")
  resolved.asInstanceOf[Dataset].getEntities.foreach((entity: Entity) => {
    println(entity.getValues)
  })

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
