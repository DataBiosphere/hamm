package org.broadinstitute.workbench.hamm
package server

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.hamm.pricing.JsonCodec._
import org.broadinstitute.workbench.hamm.pricing.{ComputeCost, GcpPricing}
import org.broadinstitute.workbench.hamm.protos.hamm._

class HammGrpcImp[F[_]: Sync: Logger](pricing: GcpPricing[F]) extends HammFs2Grpc[F] {
  override def getWorkflowCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
    for {
      priceList <- pricing.getPriceList()
      computeCost <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, ComputeCost]](priceList.asJson.as[ComputeCost].leftWiden))
    } yield {
      WorkflowCostResponse(computeCost.totalCost)
    }
  }

  override def status(request: StatusRequest, clientHeaders: Metadata): F[StatusResponse] = Sync[F].point(StatusResponse(
    BuildInfo.scalaVersion,
    BuildInfo.sbtVersion, 
    BuildInfo.gitHeadCommit.getOrElse("No commit yet"),
    BuildInfo.buildTime,
    BuildInfo.toString
  ))
}
