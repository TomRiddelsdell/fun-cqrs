package decomposition.domain.instrument

import java.util.UUID
import io.funcqrs.AggregateId

/** Defines the a type-safe ID for Instrument Aggregate */
case class InstrumentId(value: String) extends AggregateId

object InstrumentId {

  /** build a InstrumentId from a String */
  def fromString(aggregateId: String): InstrumentId = InstrumentId(aggregateId)

  /** generate a random InstrumentId */
  def generate(): InstrumentId = InstrumentId(UUID.randomUUID().toString)
}
