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

  val resolved = resolver.resolve(initialPuzzle)

  if (resolver.completelyResolved) {
    announcer.announceGood("Successfully resolved!")
    resolved.asInstanceOf[Dataset].getEntities.foreach((entity: Entity) => {
      println(entity.getValues)
    })
  } else {
    announcer.announceBad("Sorry, couldn't resolve this one! You're on your own!")
  }

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
