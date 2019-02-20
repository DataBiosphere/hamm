package org.broadinstitute.workbench.hamm.dao


import cats.effect.IO
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec.http4sMetadataResponseDecoder
import org.broadinstitute.workbench.hamm.model.{MetadataResponse, UserInfo, WorkflowId}
import org.http4s.{AuthScheme, Credentials, Uri}
import cats.effect._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType
import org.http4s.dsl.io._
import org.http4s.headers.{Accept, Authorization}

class WorkflowMetadataDAO(httpClient: Client[IO], uri: Uri) {
  def getMetadata(userInfo: UserInfo, workflowId: WorkflowId): MetadataResponse = {
    val url = uri + "/" + workflowId.uuid.toString + "/metadata"
    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, userInfo.token)), Accept(MediaType.application.json))
    httpClient.expect[MetadataResponse](request)(http4sMetadataResponseDecoder).unsafeRunSync()
  }
}
