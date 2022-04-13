package decomposition.akka.persistence

import akka.persistence.journal.{ Tagged, WriteEventAdapter }
import decomposition.domain.instrument.{ Instrument, InstrumentEvent }

class TagWriteEventAdapter extends WriteEventAdapter {

  def manifest(event: Any): String = event.getClass.getName

  def toJournal(event: Any): Any = {
    event match {
      // all lottery events get tagged with lottery tag!
      case evt: InstrumentEvent => Tagged(evt, Set(Instrument.tag.value))
      case evt                  => evt
    }
  }
}
