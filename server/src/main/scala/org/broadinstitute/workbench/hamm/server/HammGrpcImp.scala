package org.broadinstitute.workbench.hamm
package server

import java.time.Instant

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.hamm.CostCalculator
import org.broadinstitute.workbench.hamm.pricing.JsonCodec._
import org.broadinstitute.workbench.hamm.pricing.{GcpPricing, PriceList}
import org.broadinstitute.workbench.hamm.protos.hamm._

class HammGrpcImp[F[_]: Sync: Logger](pricing: GcpPricing[F]) extends HammFs2Grpc[F] {
  override def getWorkflowCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
    for {
      //cromwellMetadata: MetadataResponse <- ???
      rawPriceList <- pricing.getGcpPriceList()
      priceList <-  Sync[F].rethrow(Sync[F].delay[Either[Throwable, PriceList]](GcpPricing.getPriceList(rawPriceList, List(), List())))
      result <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, Double]](CostCalculator.getPriceOfWorkflow(sampleMetaData, priceList)))
    } yield {
      WorkflowCostResponse(result)
    }
  }

  override def status(request: StatusRequest, clientHeaders: Metadata): F[StatusResponse] = Sync[F].point(StatusResponse(
    BuildInfo.scalaVersion,
    BuildInfo.sbtVersion, 
    BuildInfo.gitHeadCommit.getOrElse("No commit yet"),
    BuildInfo.buildTime,
    BuildInfo.toString
  ))


  // ToDo: REMOVE THIS WHEN WE GET THE METADATA HOOKED UP ABOVE - I'm just passing in a sample one right now so it compiles
  val sampleMetaData = MetadataResponse(
    List(Call(
      RuntimeAttributes(CpuNumber(1), Disk(DiskName("local-disk"), DiskSize(1), DiskType.HDD), BootDiskSizeGb(10), PreemptibleAttemptsAllowed(3)),
      List(),
      false,
      true,
      Region.Uscentral1,
      Status.Done,
      MachineType.F1Micro,
      BackEnd.Jes,
      Attempt(1))),
    Instant.now,
    Instant.now
  )
}
