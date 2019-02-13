package org.broadinstitute.workbench.hamm.api


import cats.effect._
import io.circe.generic.auto._
import org.broadinstitute.workbench.hamm.service.{StatusResponse, StatusService, WorkflowCostResponse, WorkflowCostService}
import org.http4s.HttpRoutes
import org.http4s.headers.Allow
import org.http4s.server.Router
import cats.effect._
import org.broadinstitute.workbench.hamm.model.WorkflowId
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.syntax.kleisli._


class HammRoutes[F[_]: Sync](workflowCostService: WorkflowCostService[F], statusService: StatusService[F]) extends Http4sDsl[F] {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  // service with the longest matching prefix.
  def routes =
    Router[F](
      "/status" -> statusRoute,
      "/workflow" -> authedRoutes
    ).orNotFound

  def statusRoute: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(statusService.status())
      case _ -> Root =>
        // The default route result is NotFound. Sometimes MethodNotAllowed is more appropriate.
        MethodNotAllowed(Allow(GET))
    }

  def authedRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / UUIDVar(workflowId) =>
        Ok(workflowCostService.getWorkflowCost(WorkflowId(workflowId)))
    }

  implicit val statusResponseEncoder: EntityEncoder[F, StatusResponse] = jsonEncoderOf[F, StatusResponse]
  implicit val workflowCostResponseResponseEncoder: EntityEncoder[F, WorkflowCostResponse] = jsonEncoderOf[F, WorkflowCostResponse]
}