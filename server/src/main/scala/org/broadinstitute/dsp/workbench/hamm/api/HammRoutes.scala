package org.broadinstitute.dsp.workbench.hamm.api

import cats.data.{Kleisli, OptionT}
import cats.effect._
import io.circe.generic.auto._
import org.broadinstitute.dsp.workbench.hamm.model.{HammException, WorkflowId}
import org.broadinstitute.dsp.workbench.hamm.service._
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import org.broadinstitute.dsp.workbench.hamm.auth.SamAuthProvider
import org.http4s.Credentials.Token
import org.http4s.{AuthScheme, AuthedService, HttpRoutes, Request, Response, Status}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._
import org.http4s.circe.CirceEntityEncoder._
import org.broadinstitute.dsp.workbench.hamm.api.HammRoutes.{authed, handleException}
import org.broadinstitute.dsp.workbench.hamm.db.CallFqn

class HammRoutes(samDAO: SamAuthProvider, costService: CostService, statusService: StatusService)(implicit con: Concurrent[IO]) extends Http4sDsl[IO] with HammLogger {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  //  service with the longest matching prefix.
  def routes = Logger[IO](true, true)( Router[IO](
    "/status" -> statusRoute,
    "/api/cost/v1" -> authed(costRoutes)
  ).orNotFound).mapF(handleException)


  def statusRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok( IO { statusService.status() } )
  }


  def costRoutes: AuthedService[Token, IO] = AuthedService.apply {
    case GET -> Root / "workflow" / workflowId as userToken =>
      Ok(IO { costService.getWorkflowCost(userToken, WorkflowId(workflowId)) })
    case GET -> Root / "job" / workflowId / callFqn / attempt / IntVar(jobIndex)  as userToken =>
      Ok(IO { costService.getJobCost(userToken, WorkflowId(workflowId), CallFqn(callFqn), attempt.toShort, jobIndex) })
  }

}

// ToDo: Add some tests for these
object HammRoutes extends HammLogger with Http4sDsl[IO] {

  // middleware that extracts the token from the request
  def extractToken(request: Request[IO]): Token = {
    val unauthorizedException = HammException(Status.Unauthorized.code, "User is unauthorized.")
    request.headers.get(`Authorization`).getOrElse(throw unauthorizedException).credentials match {
      case tokenCred: Token if tokenCred.authScheme.equals(AuthScheme.Bearer)=> tokenCred
      case _ => throw unauthorizedException
    }
  }

  val extractToken: Kleisli[OptionT[IO, ?], Request[IO], Token] =
    Kleisli(req => OptionT.liftF( IO { extractToken(req) } ))

  val authed: AuthMiddleware[IO, Token] = AuthMiddleware(extractToken)


  def handleException: IO[Response[IO]] => IO[Response[IO]] = {
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


}