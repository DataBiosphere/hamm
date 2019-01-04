package org.broadinstitute.workbench.ccm

import cats.effect.Sync
import org.http4s.client.Client
import org.http4s.circe.CirceEntityDecoder._
import JsonCodec._

class WorkflowMetadata[F[_]: Sync](httpClient: Client[F]) {
  def getMetadata(workflowId: WorkflowId): F[MetadataResponse] = {
    httpClient.expect[MetadataResponse](s"https://cromwell.dsde-alpha.broadinstitute.org/api/workflows/v1/${workflowId.id}/metadata") //TODO: figure out how to get the url properly
  }
}
