package org.broadinstitute.workbench.ccm

import cats.effect.Sync
import cats.implicits._
import io.grpc.Metadata
import org.broadinstitute.workbench.ccm.pricing.{ComputeCost, GcpPricing}
import org.broadinstitute.workbench.ccm.protos.workflow.{WorkflowCostRequest, WorkflowCostResponse, WorkflowFs2Grpc}
import pricing.model._

class WorkflowImp[F[_]: Sync](pricing: GcpPricing[F]) extends WorkflowFs2Grpc[F] {
  override def getCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
    for {
      priceList <- pricing.getPriceList()
      computeCost <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, ComputeCost]](priceList.asJson.as[ComputeCost].leftWiden))
    } yield {
      WorkflowCostResponse(computeCost.totalCost)
    }
  }
}
