package org.broadinstitute.workbench.hamm
package costUpdater

import cats.implicits._
import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger
import io.circe.Encoder
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityEncoder._
import fs2.concurrent.InspectableQueue
import org.broadinstitute.dsde.workbench.google2.Event
import org.broadinstitute.workbench.hamm.core.BuildInfo
import org.broadinstitute.workbench.hamm.model.MetadataResponse

class CostUpdaterService[F[_]: Sync: Logger](queue: InspectableQueue[F, Event[MetadataResponse]]) extends Http4sDsl[F] {
  implicit def costUpdaterEncoder: Encoder[CostUpdaterStatusResponse] = Encoder.forProduct3(
    "buildTime",
    "gitHeadCommit",
    "queueSize"
  )(x => CostUpdaterStatusResponse.unapply(x).get)

  val service: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" =>
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
  def apply[F[_]: Sync: Logger](queue: InspectableQueue[F, Event[MetadataResponse]]): CostUpdaterService[F] = new CostUpdaterService[F](queue)
}

final case class CostUpdaterStatusResponse(
    buildTime: String,
    gitHeadCommit: String,
    queueSize: Int)