package org.broadinstitute.workbench.hamm.dao


import cats.effect.IO
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec.http4sMetadataResponseDecoder
import org.broadinstitute.workbench.hamm.model.{MetadataResponse, WorkflowId}
import org.http4s.{AuthScheme, Credentials, Uri}
import cats.effect._
import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.config.CromwellConfig
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType
import org.http4s.dsl.io._
import org.http4s.headers.{Accept, Authorization}

class WorkflowMetadataDAO(httpClient: Client[IO], config: CromwellConfig) extends HammLogger {
  def getMetadata(token: String, workflowId: WorkflowId): MetadataResponse = {
    val url = config.cromwellUrl + "/api/workflows/v1/" + workflowId.id + "/metadata"
    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, token)), Accept(MediaType.application.json))
    httpClient.expect[MetadataResponse](request)(http4sMetadataResponseDecoder).unsafeRunSync()
  }
}
