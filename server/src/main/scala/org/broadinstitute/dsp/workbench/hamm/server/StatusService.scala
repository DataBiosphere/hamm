package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class StatusService[F[_]: Sync] extends Http4sDsl[F] {
  val service: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      //TODO: Return database status
      Ok()
  }
}

object StatusService {
  def apply[F[_]: Sync]: StatusService[F] = new StatusService[F]
}
