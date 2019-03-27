package org.broadinstitute.dsp.workbench.hamm
package dao

import cats.effect.Sync
import io.circe.Encoder
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityEncoder._
import StatusService._

class StatusService[F[_]: Sync] extends Http4sDsl[F] {
  val status: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      val response = StatusResponse(
        BuildInfo.buildTime.toString,
        BuildInfo.gitHeadCommit
      )

      Ok(response)
  }
}

object StatusService {
  def apply[F[_]: Sync]: StatusService[F] = new StatusService[F]

  implicit def statusResponseEncoder: Encoder[StatusResponse] = Encoder.forProduct2(
    "buildTime",
    "gitHeadCommit"
  )(x => StatusResponse.unapply(x).get)
}

final case class StatusResponse(buildTime: String, gitHeadCommit: String)
