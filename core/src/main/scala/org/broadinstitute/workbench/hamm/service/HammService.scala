package org.broadinstitute.workbench.hamm.service

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc.Metadata
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.model._

abstract class AuthedService[F[_]: Sync: Logger](samDAO: HttpSamDAO[F]) {
  //implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  private val bearerPattern = """(?i)bearer (.*)""".r

  //val samDAO = new HttpSamDAO[F](httpClient, Uri.unsafeFromString("https://sam.dsde-dev.broadinstitute.org")) // ToDo: put this in config


  def getComputePriceKeysFromMetadata(metadata: MetadataResponse): List[ComputePriceKey] = {
    metadata.calls.map { call =>
      ComputePriceKey(call.region, call.machineType, UsageType.booleanToUsageType(call.preemptible))
    }
  }


  def getStoragePriceKeysFromMetadata(metadata: MetadataResponse): List[StoragePriceKey] = {
    metadata.calls.map { call =>
      StoragePriceKey(call.region, call.runtimeAttributes.disks.diskType)
    }
  }

  def checkAuthorization(workflowCollection: WorkflowCollectionId, action: String, token: String): F[Boolean] = {
    samDAO.queryAction(SamResource(workflowCollection.uuid.toString), action, token)
  }

  def withAuthenticatedUser[T](clientHeaders: Metadata)(f: UserInfo => F[T]): F[T] = {
    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
    for {
      status <- samDAO.getUserStatus(token)
      result <- f(UserInfo.apply(status, token))
    } yield result
  }


}
