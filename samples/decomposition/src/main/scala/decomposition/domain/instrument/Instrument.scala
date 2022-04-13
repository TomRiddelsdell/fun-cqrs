package decomposition.domain.instrument

import java.time.OffsetDateTime
import java.util.UUID

import io.funcqrs._
import io.funcqrs.behavior.Types
import io.funcqrs.behavior._
import io.funcqrs.behavior.handlers._

import scala.concurrent.Future
import scala.util.{ Random, Try }

sealed trait Instrument {
  def id: InstrumentId
}

case class ActiveInstrument(id: InstrumentId) extends Instrument {

  /**
    * Action: expire instrument
    * Applicable as long as we arn't already expired
    */
  def expire =
    Instrument.actions
      .commandHandler {
        OneEvent {
          case Expire() => Expired(id)
        }
      }
      .eventHandler {
        case Expired(id, _) =>
          ExpiredInstrument(
            id           = id
          )
      }
}

case class ExpiredInstrument(id: InstrumentId) extends Instrument {

  /**
    * Action: reject double expiration
    */
  def rejectDoubleExpire = {

    Instrument.actions
      .rejectCommand {
        // can't add participant twice
        case Expire() =>
          new InstrumentAlreadyExpired("""Instrument already expired!""")
      }
  }
}

object Instrument extends Types[Instrument] {

  type Id      = InstrumentId
  type Command = InstrumentCommand
  type Event   = InstrumentEvent

  // a tag for instrument, useful to query the event store later on
  val tag = Tags.aggregateTag("instrument")

  def create(instrumentId: InstrumentId) = {
    actions
      .commandHandler {
        OneEvent { case CreateInstrument => InstrumentCreated(instrumentId) }
      }
      .eventHandler {
        case _: InstrumentCreated => ActiveInstrument(instrumentId)
      }
  }

  def behavior(instrumentId: InstrumentId): Behavior[Instrument, InstrumentCommand, InstrumentEvent] =
    Behavior
      .first { // defines how to construct a Raffle Aggregate
        create(instrumentId)
      }
      // defines how to update it
      .andThen {
        case inst: ActiveInstrument =>
          inst.expire

        case inst: ExpiredInstrument =>
          inst.rejectDoubleExpire
      }
}

class InstrumentAlreadyExpired(msg: String) extends RuntimeException(msg)
