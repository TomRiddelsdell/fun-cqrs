package decomposition.domain.instrument

import java.time.OffsetDateTime

// Events ============================================================
sealed trait InstrumentEvent {
  def instrumentId: InstrumentId
}

// Creation Event
case class InstrumentCreated(instrumentId: InstrumentId) extends InstrumentEvent

// Update Events
sealed trait InstrumentUpdateEvent extends InstrumentEvent
case class Expired(instrumentId: InstrumentId, time: java.time.OffsetDateTime = java.time.OffsetDateTime.now()) extends InstrumentUpdateEvent
case class CorporateActionApplied(corporateActionInfo: String, instrumentId: InstrumentId) extends InstrumentUpdateEvent

