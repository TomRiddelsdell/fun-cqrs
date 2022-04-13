package decomposition.repositories

import decomposition.domain.instrument.{ InstrumentId, InstrumentView }
import decomposition.repositories.InMemoryRepository

class InstrumentViewRepo extends InMemoryRepository {

  type Identifier = InstrumentId
  type Model      = InstrumentView

  /** Extract id from Model */
  protected def $id(view: InstrumentView): InstrumentId = view.id
}
