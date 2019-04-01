package org.broadinstitute.dsp.workbench.hamm
package costUpdater

import cats.effect.Sync
import cats.implicits._
import fs2.concurrent.InspectableQueue
import io.chrisdavenport.log4cats.Logger
import io.circe.Encoder
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import CostUpdaterService._

class CostUpdaterService[F[_]: Sync: Logger, A](queue: InspectableQueue[F, A]) extends Http4sDsl[F] {
  val service: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" =>
        Ok() //TODO: add DB check
      case GET -> Root / "version" =>
        for {
          size <- queue.getSize
          result <- Ok(CostUpdaterStatusResponse(
            BuildInfo.buildTime.toString,
            BuildInfo.gitHeadCommit,
            size
          ))
        } yield result
    }
  }
}

object CostUpdaterService {
  def apply[F[_]: Sync: Logger, A](queue: InspectableQueue[F, A]): CostUpdaterService[F, A] = new CostUpdaterService[F, A](queue)

  implicit def costUpdaterEncoder: Encoder[CostUpdaterStatusResponse] = Encoder.forProduct3(
    "buildTime",
    "gitHeadCommit",
    "queueSize"
  )(x => CostUpdaterStatusResponse.unapply(x).get)
}

final case class CostUpdaterStatusResponse(
    buildTime: String,
    gitHeadCommit: String,
    queueSize: Int)