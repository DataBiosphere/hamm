package org.broadinstitute.workbench.hamm.service

import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger

class StatusService[F[_]: Sync: Logger]  {

  def status(): F[StatusResponse] =  {
    Sync[F].point(StatusResponse())
  }
}

final case class StatusResponse()
