package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.Sync
import io.circe.generic.auto._
import org.broadinstitute.dsp.workbench.hamm.db.CallFqn
import org.broadinstitute.dsp.workbench.hamm.model.WorkflowId
import org.http4s.AuthedService
import org.http4s.Credentials.Token
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl

class CostService[F[_]: Sync](costDbDao: CostDbDao) extends Http4sDsl[F] {
  val service: AuthedService[Token, F] = AuthedService.apply{
    case GET -> Root / "workflow" / workflowId as userToken =>
      Ok(Sync[F].delay { costDbDao.getWorkflowCost(userToken, WorkflowId(workflowId)) })
    case GET -> Root / "job" / workflowId / callFqn / attempt / IntVar(jobIndex)  as userToken =>
      Ok(Sync[F].delay { costDbDao.getJobCost(userToken, WorkflowId(workflowId), CallFqn(callFqn), attempt.toShort, jobIndex) })
  }
}

object CostService {
  def apply[F[_]: Sync](costDbDao: CostDbDao): CostService[F] = new CostService[F](costDbDao)
}
