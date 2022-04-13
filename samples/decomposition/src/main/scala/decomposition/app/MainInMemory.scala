package decomposition.app

import decomposition.domain.instrument._
import decomposition.domain.instrument.{ Instrument, InstrumentId }

import scala.util.{ Failure, Success }

object MainInMemory extends App {

  val id = InstrumentId.generate()

  val lotteryRef = AppContext.inMemoryBackend.aggregateRef[Instrument].forId(id)

  lotteryRef ! CreateInstrument

  // add participants
  lotteryRef ! Expire()

  // ---------------------------------------------
  // fetch read model
  val viewResult = AppContext.instrumentViewRepo.find(id)

  viewResult match {
    case Success(res) => println(s" => result: $res")
    case Failure(ex)  => println(s"FAILED: ${ex.getMessage}")
  }

}
