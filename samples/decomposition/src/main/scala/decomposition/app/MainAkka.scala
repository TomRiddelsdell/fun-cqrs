package decomposition.app

import akka.actor.ActorSystem
import decomposition.domain.instrument._

import scala.concurrent.Await
import scala.util.{ Failure, Success }
import scala.concurrent.duration._

object MainAkka extends App {

  val id = InstrumentId.generate()

  val actorSys: ActorSystem = ActorSystem("FunCQRS")
  val backend               = AppContext.akkaBackend(actorSys)

  val lotteryRef = backend.aggregateRef[Instrument].forId(id)

  lotteryRef ! CreateInstrument

  // add participants
  lotteryRef ! Expire()

  Thread.sleep(3000)

  // ---------------------------------------------
  // fetch read model
  val viewResult = AppContext.instrumentViewRepo.find(id)

  viewResult match {
    case Success(res) => println(s" => result: $res")
    case Failure(ex)  => println(s"FAILED: ${ex.getMessage}")
  }

  Await.ready(actorSys.terminate(), 5.seconds)

}
