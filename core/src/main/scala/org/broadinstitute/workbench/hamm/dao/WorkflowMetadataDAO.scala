package org.broadinstitute.workbench.hamm.dao

import cats.effect.Sync
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec._
import org.broadinstitute.workbench.hamm.model.{MetadataResponse, WorkflowId}
import org.http4s.Method._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers._
import org.http4s.{AuthScheme, Credentials, Uri}

// This is fallback for serving cost API when we don't find cost info in database
class WorkflowMetadataDAO[F[_]: Sync](httpClient: Client[F]) extends Http4sClientDsl[F] {
  def getMetadata(workflowId: WorkflowId): F[MetadataResponse] = {
    val uri = Uri.unsafeFromString(s"https://cromwell.dsde-alpha.broadinstitute.org/api/workflows/v1/${workflowId.uuid}/metadata")
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
          "backend")
        )
        .withQueryParam("expandSubWorkflows", true)

    val request = GET(
      uri,
      Authorization(Credentials.Token(AuthScheme.Bearer, "ya29.Gl2xBueI7wZeIyZ7pOOnMqf-sVum_dF3mbdzzS4ZnSVlcEC6TDA17Z_fWqWnAJIY1GFD7zWTrfGdRZXe5TKru8EymyT9NFnJVNniGbimkC2-DejAI6zNTHxJlA17T3s")) //TODO: figure out proper authentication
    )
    httpClient.expect[MetadataResponse](request)
  }
}
