package org.broadinstitute.workbench.hamm.auth

import cats.effect.Sync
import org.broadinstitute.workbench.hamm.model._
import org.broadinstitute.workbench.hamm.model.SamJsonCodec.SamUserInfoResponseDecoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.{Header, Headers, Method, Request, Uri}
import org.http4s.client.Client


class HttpSamDAO[F[_]: Sync](httpClient: Client[F]) {

  def getUserStatus(token: String): F[SamUserInfoResponse] = {
    httpClient.expect[SamUserInfoResponse](
      Request[F](
        Method.GET,
        Uri.unsafeFromString("https://sam.dsde-dev.broadinstitute.org/register/user/v2/self/info"),
        headers = Headers(Header("authorization", token))))
  }

  def queryAction(samResource: SamResource, action: String, token: String): F[Boolean] = {
      httpClient.expect[Boolean](
        Request[F](
          Method.GET,
          Uri.unsafeFromString(s"https://sam.dsde-dev.broadinstitute.org/api/resources/v1/workflow-collection/${samResource.resourceName}/action/$action"),
          headers = Headers(Header("authorization", token))))
  }

}

