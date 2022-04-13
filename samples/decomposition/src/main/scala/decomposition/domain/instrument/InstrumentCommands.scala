package decomposition.domain.instrument

/* all Commands it may receive and Events it may emit */
sealed trait InstrumentCommand

case object CreateInstrument extends InstrumentCommand

case class Expire() extends InstrumentCommand

