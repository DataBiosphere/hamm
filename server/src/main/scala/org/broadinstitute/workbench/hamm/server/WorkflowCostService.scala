package org.broadinstitute.workbench.hamm
package server

import java.time.Instant
import java.util.UUID

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.dao.GooglePriceListDAO
import org.broadinstitute.workbench.hamm.model._
import org.broadinstitute.workbench.hamm.protos.hamm._

class WorkflowCostService[F[_]: Sync: Logger](pricing: GooglePriceListDAO[F], samDAO: HttpSamDAO[F]) extends HammFs2Grpc[F] {

  override def getWorkflowCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
    withAuthenticatedUser1(clientHeaders) { userInfo =>
      for {
        userInfo <- withAuthenticatedUser(clientHeaders)
        //        cromwellMetadata: MetadataResponse <- ???
        _ <- checkAuthorization(sampleMetaData.workflowCollectionId, "get_cost", userInfo.token)
        rawPriceList <- pricing.getGcpPriceList()
        priceList <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, PriceList]](GooglePriceListDAO.parsePriceList(rawPriceList, List(ComputePriceKey(Region.Australiasoutheast1, MachineType.N1Standard, UsageType.Preemptible)), List(StoragePriceKey(Region.Australiasoutheast1, DiskType.HDD)))))
        result <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, Double]](CostCalculator.getPriceOfWorkflow(sampleMetaData, priceList)))
      } yield {
        WorkflowCostResponse(result)
      }
    }
  }

  override def status(request: StatusRequest, clientHeaders: Metadata): F[StatusResponse] =  {
    Sync[F].point(StatusResponse(
      BuildInfo.scalaVersion,
      BuildInfo.sbtVersion,
      BuildInfo.gitHeadCommit.getOrElse("No commit yet"),
      BuildInfo.buildTime,
      BuildInfo.toString
    ))
  }

  def checkAuthorization(workflowCollection: WorkflowCollectionId, action: String, token: String): F[Boolean] = {
   for {
      authorized <- samDAO.queryAction(SamResource(workflowCollection.uuid.toString), action, token)
    } yield {
     authorized
   }
  }

  def withAuthenticatedUser1[T](clientHeaders: Metadata)(f: UserInfo => F[T]): F[T] = {
    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
    for {
      status <- samDAO.getUserStatus(token)
    } yield {
      f(UserInfo.apply(status, token))
    }
  }

  def withAuthenticatedUser(clientHeaders: Metadata): F[UserInfo] = {
    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
    for {
      status <- samDAO.getUserStatus(token)
    } yield {
      UserInfo.apply(status, token)
    }
  }


//  override def authedEndpoint(request: AuthedEndpointRequest, clientHeaders: Metadata): F[AuthedEndpointResponse] = {
//    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
//    for {
//      status <- samDAO.getUserStatus(token)
//    } yield {
//      AuthedEndpointResponse(status.toString)
//    }
//  }


  // ToDo: REMOVE THIS WHEN WE GET THE METADATA HOOKED UP ABOVE - I'm just passing in a sample one right now so it compiles
  val sampleMetaData = MetadataResponse(
    List(Call(
      RuntimeAttributes(CpuNumber(1), Disk(DiskName("local-disk"), DiskSize(1), DiskType.HDD), BootDiskSizeGb(10), PreemptibleAttemptsAllowed(3)),
      List(),
      false,
      true,
      Region.Australiasoutheast1,
      Status.Done,
      MachineType.N1Standard,
      BackEnd.Jes,
      Attempt(1))),
    Instant.now,
    Instant.now,
    WorkflowCollectionId(UUID.fromString("")),
    Map()
  )
}
