package org.broadinstitute.workbench.hamm.dao


import cats.effect.IO
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec.http4sMetadataResponseDecoder
import org.broadinstitute.workbench.hamm.model.{MetadataResponse, WorkflowId}
import org.http4s.{AuthScheme, Credentials, Uri}
import cats.effect._
import org.broadinstitute.workbench.hamm.HammLogger
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType
import org.http4s.dsl.io._
import org.http4s.headers.{Accept, Authorization}

class WorkflowMetadataDAO(httpClient: Client[IO], uri: Uri) extends HammLogger {
  def getMetadata(token: String, workflowId: WorkflowId): MetadataResponse = {
    val url = uri + "/" + workflowId.id + "/metadata"
    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, token)), Accept(MediaType.application.json))
    logger.info("URL: " + url)
    logger.info("REQUEST: " + request.toString)
    httpClient.expect[MetadataResponse](request)(http4sMetadataResponseDecoder).unsafeRunSync()
  }
}
