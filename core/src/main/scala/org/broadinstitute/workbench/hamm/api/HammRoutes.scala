package org.broadinstitute.workbench.hamm.api


import cats.data.{Kleisli, OptionT}
import cats.effect._
import io.circe.generic.auto._
import org.broadinstitute.workbench.hamm.service.{StatusResponse, StatusService, WorkflowCostResponse, WorkflowCostService}
import org.http4s.HttpRoutes
import org.http4s.headers.Allow
import org.http4s.server.{AuthMiddleware, Router}
import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.model.{HammException, UserInfo, WorkflowId}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.server.middleware.RequestLogger
import org.http4s.syntax.kleisli._
import org.http4s.util.CaseInsensitiveString

class HammRoutes(samDAO: HttpSamDAO, workflowCostService: WorkflowCostService, statusService: StatusService)(implicit con: Concurrent[IO]) extends Http4sDsl[IO] with HammLogger {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  // service with the longest matching prefix.
  def routes = RequestLogger[IO](true, true)( Router[IO](
    "/status" -> statusRoute,
    "/workflow" -> authorize(workflowRoutes)
  ).orNotFound).mapF(handleException)


  def statusRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok( IO { statusService.status() } )
    case _ -> Root =>
      // The default route result is NotFound. Sometimes MethodNotAllowed is more appropriate.
      MethodNotAllowed(Allow(GET))
  }

  def workflowRoutes: AuthedService[UserInfo, IO] = AuthedService.apply {
    case GET -> Root / UUIDVar(workflowId) as user =>
      Ok(IO { workflowCostService.getWorkflowCost(user, WorkflowId(workflowId)) })
    case _ -> Root as user =>
      MethodNotAllowed(Allow(GET))
  }



  // ToDo: Move the auth stuff to an object
  val authUser: Kleisli[OptionT[IO, ?], Request[IO], UserInfo] =
    Kleisli(req => OptionT.liftF(IO {
      val token = req.headers.get(CaseInsensitiveString("authorization")).getOrElse(throw HammException(401, "can't find auth header")).value.split(" ").last // ToDo: fix this
      val samResponse = samDAO.getUserStatus(token)
      UserInfo.apply(samResponse, token)
    }))

  val authorize: AuthMiddleware[IO, UserInfo] = AuthMiddleware(authUser)




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

  implicit val statusResponseEncoder: EntityEncoder[IO, StatusResponse] = jsonEncoderOf[IO, StatusResponse]
  implicit val workflowCostResponseResponseEncoder: EntityEncoder[IO, WorkflowCostResponse] = jsonEncoderOf[IO, WorkflowCostResponse]


}