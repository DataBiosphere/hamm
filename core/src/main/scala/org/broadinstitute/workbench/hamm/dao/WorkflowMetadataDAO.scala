package org.broadinstitute.workbench.hamm.dao

import cats.effect.Sync
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec._
import org.broadinstitute.workbench.hamm.model.{MetadataResponse, WorkflowId}
import org.http4s.Uri
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client

class WorkflowMetadataDAO[F[_]: Sync](httpClient: Client[F], uri: Uri) {
  def getMetadata(workflowId: WorkflowId): F[MetadataResponse] = {
    httpClient.expect[MetadataResponse](uri + s"/${workflowId.uuid}/metadata") //TODO: figure out how to get the url properly
  }
}
