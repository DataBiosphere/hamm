package org.broadinstitute.dsp.workbench.hamm
package server

import cats.data.{Kleisli, OptionT}
import cats.effect._
import org.broadinstitute.dsp.workbench.hamm.model.HammException
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamAuthProvider
import org.http4s.Credentials.Token
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.middleware.Logger
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.syntax.kleisli._
import org.http4s.{AuthScheme, HttpApp, Request, Response, Status}
import HammRoutes._

class HammRoutes(samDAO: SamAuthProvider,
                 costService: CostService[IO],
                 statusService: StatusService[IO],
                 versionService: VersionService[IO])(implicit con: Concurrent[IO]) extends Http4sDsl[IO] with HammLogger {
  // A Router can mount multiple services to prefixes.  The request is passed to the
  //  service with the longest matching prefix.
  val routes: HttpApp[IO] = Logger.httpApp(true, true)( Router[IO](
    "/status" -> statusService.service,
    "/version" -> versionService.service,
    "/api/cost/v1" -> authed(costService.service)
  ).orNotFound).mapF(handleException)


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

// ToDo: Add some tests for these
object HammRoutes {
  // middleware that extracts the token from the request
  private def extractToken(request: Request[IO]): Token = {
    val unauthorizedException = HammException(Status.Unauthorized.code, "User is unauthorized.")
    request.headers.get(`Authorization`).getOrElse(throw unauthorizedException).credentials match {
      case tokenCred: Token if tokenCred.authScheme.equals(AuthScheme.Bearer)=> tokenCred
      case _ => throw unauthorizedException
    }
  }

  private val extractToken: Kleisli[OptionT[IO, ?], Request[IO], Token] =
    Kleisli(req => OptionT.liftF( IO { extractToken(req) } ))

  val authed: AuthMiddleware[IO, Token] = AuthMiddleware(extractToken)
}