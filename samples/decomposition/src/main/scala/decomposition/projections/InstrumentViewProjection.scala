package decomposition.projections

import io.funcqrs.projections.Projection
import decomposition.domain.instrument._
import decomposition.repositories.InstrumentViewRepo

import scala.concurrent.Future

class InstrumentViewProjection(repo: InstrumentViewRepo) extends Projection[InstrumentEvent] {

  def handleEvent = attempt.HandleEvent {

    case e: InstrumentCreated =>
      repo.save(InstrumentView(e.instrumentId, false, None))

    case e: InstrumentUpdateEvent =>
      repo
        .updateById(e.instrumentId) { lot =>
          updateFunc(lot, e)
        }
        .map(_ => ())
  }

  override def onFailure: OnFailure = {
    case (e, _) => Future.successful(())
  }

  private def updateFunc(view: InstrumentView, evt: InstrumentUpdateEvent): InstrumentView = {
    evt match {

      case e: Expired =>
        view.copy(isExpired = true, expiryTime = Some(e.time))

      case e: CorporateActionApplied =>
        view.copy(corporateActions = e :: view.corporateActions)

    }
  }
}

//end::lottery-view-projection[]
