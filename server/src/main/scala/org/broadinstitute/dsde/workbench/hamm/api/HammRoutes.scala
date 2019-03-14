package org.broadinstitute.dsde.workbench.hamm.api

import cats.effect._
import io.circe.generic.auto._
import org.broadinstitute.dsde.workbench.hamm.HammLogger
import org.broadinstitute.dsde.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.dsde.workbench.hamm.model.{HammException, JobId, WorkflowId}
import org.broadinstitute.dsde.workbench.hamm.service._
import org.http4s.Credentials.Token
import org.http4s.circe.jsonEncoderOf
import org.http4s.{AuthScheme, EntityEncoder, HttpRoutes, Request, Response, Status}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Allow, Authorization}
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._

class HammRoutes(samDAO: SamAuthProvider, costService: CostService, statusService: StatusService)(implicit con: Concurrent[IO]) extends Http4sDsl[IO] with HammLogger {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  //  service with the longest matching prefix.
  def routes = Logger[IO](true, true)( Router[IO](
    "/status" -> statusRoute,
    "/api/cost/v1" -> costRoutes
  ).orNotFound).mapF(handleException)


  def statusRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok( IO { statusService.status() } )
    case _ -> Root =>
      MethodNotAllowed(Allow(GET))
  }


  def costRoutes = HttpRoutes.of[IO] {
    case request @ GET -> Root / "workflow" / workflowId =>
      Ok(IO { costService.getWorkflowCost(extractToken(request), WorkflowId(workflowId)) })
    case request @ GET -> Root / "job" / jobId =>
      Ok(IO { costService.getJobCost(extractToken(request), JobId(jobId)) })
    case _ -> Root =>
      MethodNotAllowed(Allow(GET))
  }






  private def extractToken(request: Request[IO]): Token = {
    val unauthorizedException = HammException(Status.Unauthorized.code, "User is unauthorized.")

    request.headers.get(`Authorization`).getOrElse(throw unauthorizedException).credentials match {
      case tokenCred: Token if tokenCred.authScheme.equals(AuthScheme.Bearer)=> tokenCred
      case _ => throw unauthorizedException
    }
  }

  private def handleException: IO[Response[IO]] => IO[Response[IO]] = {
    x =>  x.handleErrorWith {
      case hammException: HammException => {
        logger.error(hammException)("Hamm service serror")
        Ok(hammException.regrets).map[Response[IO]](resp => resp.withStatus(Status.apply(hammException.status)))

      }
      case th: Throwable => {
        logger.error(th)("Hamm Error") // change this message
        InternalServerError(th.getMessage)
      }
      case _ => InternalServerError("Something went wrong")
    }
  }


  implicit val statusResponseEncoder: EntityEncoder[IO, StatusResponse] = jsonEncoderOf[IO, StatusResponse]
  implicit val workflowCostResponseEncoder: EntityEncoder[IO, WorkflowCostResponse] = jsonEncoderOf[IO, WorkflowCostResponse]
  implicit val jobCostResponseEncoder: EntityEncoder[IO, JobCostResponse] = jsonEncoderOf[IO, JobCostResponse]

}