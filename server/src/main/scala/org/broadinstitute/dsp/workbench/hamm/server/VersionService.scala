package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.Sync
import io.circe.Encoder
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityEncoder._
import VersionService._

class VersionService[F[_]: Sync] extends Http4sDsl[F] {
  val service: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      val response = VersionResponse(
        BuildInfo.buildTime.toString,
        BuildInfo.gitHeadCommit
      )

      Ok(response)
  }
}

object VersionService {
  def apply[F[_]: Sync]: VersionService[F] = new VersionService[F]

  implicit def statusResponseEncoder: Encoder[VersionResponse] = Encoder.forProduct2(
    "buildTime",
    "gitHeadCommit"
  )(x => VersionResponse.unapply(x).get)
}

final case class VersionResponse(buildTime: String, gitHeadCommit: String)
