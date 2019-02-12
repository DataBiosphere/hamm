package org.broadinstitute.workbench.hamm
package server

import java.util.UUID

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.model._
import org.broadinstitute.workbench.hamm.protos.hamm._

class WorkflowCostService[F[_]: Sync: Logger](pricing: GooglePriceListDAO[F],
                                              workflowDAO: WorkflowMetadataDAO[F],
                                              samDAO: HttpSamDAO[F]) extends HammFs2Grpc[F] {

  override def getWorkflowCost(request: WorkflowCostRequest, clientHeaders: Metadata): F[WorkflowCostResponse] = {
 //   withAuthenticatedUser(clientHeaders) { userInfo =>
      //ToDo: some work here to make this less messy
      for {
        userInfo <- withAuthenticatedUser(clientHeaders)
        cromwellMetadata: MetadataResponse <- workflowDAO.getMetadata(WorkflowId(UUID.fromString(request.id)))
        _ <- checkAuthorization(cromwellMetadata.workflowCollectionId, "get_cost", userInfo.token)
        rawPriceList <- pricing.getGcpPriceList()
        priceList <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, PriceList]](GooglePriceListDAO.parsePriceList(rawPriceList, getComputePriceKeysFromMetadata(cromwellMetadata), getStoragePriceKeysFromMetadata(cromwellMetadata))))
        result <- Sync[F].rethrow(Sync[F].delay[Either[Throwable, Double]](CostCalculator.getPriceOfWorkflow(cromwellMetadata, priceList)))
      } yield {
        WorkflowCostResponse(result)
      }
   // }
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



  private def getComputePriceKeysFromMetadata(metadata: MetadataResponse): List[ComputePriceKey] = {
    metadata.calls.map { call =>
      ComputePriceKey(call.region, call.machineType, UsageType.booleanToUsageType(call.preemptible))
    }
  }


  private def getStoragePriceKeysFromMetadata(metadata: MetadataResponse): List[StoragePriceKey] = {
    metadata.calls.map { call =>
      StoragePriceKey(call.region, call.runtimeAttributes.disks.diskType)
    }
  }

  private def checkAuthorization(workflowCollection: WorkflowCollectionId, action: String, token: String): F[Boolean] = {
    for {
      authorized <- samDAO.queryAction(SamResource(workflowCollection.uuid.toString), action, token)
    } yield {
      authorized
    }
  }

  //  def withAuthenticatedUser[T](clientHeaders: Metadata)(f: UserInfo => F[T]): F[T] = {
  //    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
  //    for {
  //      status <- samDAO.getUserStatus(token)
  //    } yield {
  //      f(UserInfo.apply(status, token))
  //    }
  //  }

  private def withAuthenticatedUser(clientHeaders: Metadata): F[UserInfo] = {
    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
    for {
      status <- samDAO.getUserStatus(token)
    } yield {
      UserInfo.apply(status, token)
    }
  }
}
