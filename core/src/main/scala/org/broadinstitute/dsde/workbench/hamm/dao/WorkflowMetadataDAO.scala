package org.broadinstitute.dsde.workbench.hamm.dao


import cats.effect.IO
import org.broadinstitute.dsde.workbench.hamm.model.CromwellMetadataJsonCodec.http4sMetadataResponseDecoder
import org.broadinstitute.dsde.workbench.hamm.model.{MetadataResponse, WorkflowId}
import org.broadinstitute.dsde.workbench.hamm.HammLogger
import org.broadinstitute.dsde.workbench.hamm.config.CromwellConfig
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers._
import org.http4s.{AuthScheme, Credentials, Uri}
import org.http4s.MediaType
import org.http4s.dsl.io._

// This is fallback for serving cost API when we don't find cost info in database
class WorkflowMetadataDAO(httpClient: Client[IO], config: CromwellConfig) extends HammLogger with Http4sClientDsl[IO] {
  def getMetadata(token: String, workflowId: WorkflowId): MetadataResponse = {
    val uri = Uri.unsafeFromString(s"https://cromwell.dsde-alpha.broadinstitute.org/api/workflows/v1/${workflowId.id}/metadata")
        .withQueryParam("includeKey", List(
          "id",
          "start",
          "end",
          "labels",
          "executionEvents",
          "runtimeAttributes",
          "jobId",
          "preemptible",
          "callCaching:hit",
          "hit",
          "jes",
          "papi2",
          "executionStatus",
          "attempt",
          "backend")
        )
        .withQueryParam("expandSubWorkflows", true)

    val request = GET(
      uri,
      Authorization(Credentials.Token(AuthScheme.Bearer, token)), //TODO: remove this file
      Accept(MediaType.application.json)
    )
    httpClient.expect[MetadataResponse](request)(http4sMetadataResponseDecoder).unsafeRunSync()
  }
}
