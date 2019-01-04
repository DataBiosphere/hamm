package org.broadinstitute.workbench.ccm

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.ccm.pricing.{ComputeCost, GcpPricing}
import org.broadinstitute.workbench.protos.ccm._
import pricing.JsonCodec._

class WorkflowImp[F[_]: Sync: Logger](pricing: GcpPricing[F]) extends CcmFs2Grpc[F] {
  override def getWorkflowCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
    for {
      priceList <- pricing.getPriceList()
      computeCost <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, ComputeCost]](priceList.asJson.as[ComputeCost].leftWiden))
    } yield {
      WorkflowCostResponse(computeCost.totalCost)
    }
  }
}
