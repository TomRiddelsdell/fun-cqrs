package decomposition.domain.instrument

import io.funcqrs.config.Api._
import io.funcqrs.test.InMemoryTestSupport
import io.funcqrs.test.backend.InMemoryBackend
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{OptionValues, TryValues}
import decomposition.projections.InstrumentViewProjection
import decomposition.repositories.InstrumentViewRepo

class InstrumentTest extends AnyFunSuite with Matchers with OptionValues with TryValues {

  val repo = new InstrumentViewRepo

  val id = InstrumentId("test-instrument")

  class InstrumentInMemoryTest extends InMemoryTestSupport {

    def configure(backend: InMemoryBackend): Unit = {
      // ---------------------------------------------
      // aggregate config - write model
      backend.configure {
        aggregate(Instrument.behavior)
      }

      // ---------------------------------------------
      // projection config - read model
      backend.configure {
        projection(
          projection       = new InstrumentViewProjection(repo),
          publisherFactory = backend.inMemoryPublisherFactory[InstrumentEvent],
          name             = "InstrumentViewProjection"
        )
      }
    }

    def instrumentRef(id: InstrumentId) =
      backend.aggregateRef[Instrument].forId(id)

  }

  test("Run a Instrument") {

    new InstrumentInMemoryTest {

      val instrument = instrumentRef(id)

      // send all commands
      instrument ? CreateInstrument
      instrument ? Expire()


      // assert that expected events were produced
      expectEvent[InstrumentCreated]
      expectEventPF { case Expired(_,_) => () }

      // check the view projection
      val view = repo.find(id).success.value
      view.isExpired shouldBe true
      view.corporateActions shouldBe empty
      view.expiryTime shouldBe defined
    }

  }

  test("Expire a instrument twice") {

    new InstrumentInMemoryTest {

      val instrument = instrumentRef(id)

      instrument ? CreateInstrument
      instrument ? Expire()

      intercept[InstrumentAlreadyExpired] {
        instrument ? Expire()
      }
    }

  }

  /*test("Corporate Actions") {

    new InstrumentInMemoryTest {

      val instrument = instrumentRef(id)

      instrument ? CreateInstrument
      instrument ? ApplyCorporateAction("Second CA")
      instrument ? ApplyCorporateAction("First CA")

      val view = repo.find(id).success.value
      view.corporateActions should have size 2
      view.corporateActions[0].corporateActionInfo shouldBe "Second CA"
      view.corporateActions[1].corporateActionInfo shouldBe "First CA"
    }

  }*/
  
}
