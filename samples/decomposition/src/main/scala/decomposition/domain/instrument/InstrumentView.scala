package decomposition.domain.instrument

import java.time.OffsetDateTime

case class InstrumentView(
    id: InstrumentId,
    isExpired: Boolean,
    expiryTime: Option[java.time.OffsetDateTime],
    corporateActions: List[CorporateActionApplied] = List[CorporateActionApplied]()
) {

  override def toString: String = {
    def cas = corporateActions.map(_.corporateActionInfo).mkString(" | ")

    s"""
       |InstrumentView
       |  id: $id
       |  isExpired: $isExpired
       |  expireTime: $expiryTime
       |  corporateActions: $cas
     """.stripMargin
  }
}

