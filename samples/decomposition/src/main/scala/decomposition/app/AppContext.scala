package decomposition.app

import akka.actor.ActorSystem
//import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
//import com.github.j5ik2o.akka.persistence.dynamodb.query.DynamoDBReadJournalProvider
import com.github.j5ik2o.akka.persistence.dynamodb.query.scaladsl.{ DynamoDBReadJournal => ScalaDynamoDBReadJournal }
import akka.persistence.query.{PersistenceQuery, Sequence}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.funcqrs.akka.backend.AkkaBackend
import io.funcqrs.config.AkkaOffsetPersistenceStrategy
import io.funcqrs.config.Api.{projection, _}
import io.funcqrs.projections.PublisherFactory
import io.funcqrs.test.backend.InMemoryBackend
import org.reactivestreams.Publisher
import decomposition.domain.instrument.{Instrument, InstrumentEvent}
import decomposition.projections.InstrumentViewProjection
import decomposition.repositories.InstrumentViewRepo

import scala.language.higherKinds

object AppContext {

  val instrumentViewRepo = new InstrumentViewRepo

  def akkaBackend(actorSys: ActorSystem) = {

    implicit val system = actorSys

    val backend = new AkkaBackend {
      val actorSystem: ActorSystem = actorSys
    }

    val readJournal = PersistenceQuery(actorSys).readJournalFor[ScalaDynamoDBReadJournal](ScalaDynamoDBReadJournal.Identifier)

    backend
      .configure {
        // aggregate config - write model
        aggregate(Instrument.behavior)
      }
      .configure {
        projection(
          projection = new InstrumentViewProjection(instrumentViewRepo),
          publisherFactory = new PublisherFactory[Long, InstrumentEvent] {
            override def from(offset: Option[Long]): Publisher[(Long, InstrumentEvent)] = {

              val akkaOffset = offset.map(Sequence).getOrElse(Sequence(0))

              readJournal
                .eventsByTag(Instrument.tag.value, akkaOffset)
                .map { akkaEnvelope =>
                  val Sequence(value) = akkaEnvelope.offset
                  (value, akkaEnvelope.event.asInstanceOf[InstrumentEvent])
                }
                .runWith(Sink.asPublisher(false))

            }
          }
        ).withCustomOffsetPersistence(
          AkkaOffsetPersistenceStrategy.offsetAsLong(actorSys, "instrument-view")
        )

      }
  }




  lazy val inMemoryBackend = {
    val backend = new InMemoryBackend
    backend
      .configure {
        // aggregate config - write model
        aggregate(Instrument.behavior)
      }
      .configure {
        projection(
          projection = new InstrumentViewProjection(instrumentViewRepo),
          publisherFactory = backend.inMemoryPublisherFactory[InstrumentEvent]
        )
      }
  }

}
