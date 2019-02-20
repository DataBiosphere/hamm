package org.broadinstitute.workbench.hamm.auth


import cats.effect.IO
import org.broadinstitute.workbench.hamm.model._
import org.broadinstitute.workbench.hamm.model.SamJsonCodec.SamUserInfoResponseDecoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.{AuthScheme, Credentials, Uri}
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType
import org.http4s.dsl.io._

class HttpSamDAO(httpClient: Client[IO], uri: Uri) {

  def getUserStatus(token: String): SamUserInfoResponse = {
    val url = uri + "/register/user/v2/self/info"
    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, token)), Accept(MediaType.application.json))
    httpClient.expect[SamUserInfoResponse](request).unsafeRunSync()

//    httpClient.expect[SamUserInfoResponse] (
//      Request[IO](
//        Method.GET,
//        Uri.unsafeFromString(uri + "/register/user/v2/self/info"),
//        headers = Headers(Header("authorization", token))))
//      .unsafeRunSync()

  }

  def queryAction(token: String, samResource: SamResource, action: String): Boolean = {
    val url = uri + s"/api/resources/v1/workflow-collection/${samResource.resourceName}/action/$action"
    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, token)), Accept(MediaType.application.json))
    httpClient.expect[Boolean](request).unsafeRunSync()

//    httpClient.expect[Boolean](
//      Request[IO](
//        Method.GET,
//        Uri.unsafeFromString(url),
//        headers = Headers(Header("authorization", token))))
//      .unsafeRunSync()
  }

//  def run[T](req: IO[T]): T = {
//    req.unsafeRunSync()
//  }

}

