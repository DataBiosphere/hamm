package org.broadinstitute.workbench.hamm.api


import cats.effect._
import io.circe.generic.auto._
import org.broadinstitute.workbench.hamm.service.{StatusResponse, StatusService, WorkflowCostResponse, WorkflowCostService}
import org.http4s.HttpRoutes
import org.http4s.headers.Allow
import org.http4s.server.Router
import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.workbench.hamm.model.{HammException, WorkflowId}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._

class HammRoutes(samDAO: SamAuthProvider, workflowCostService: WorkflowCostService, statusService: StatusService)(implicit con: Concurrent[IO]) extends Http4sDsl[IO] with HammLogger {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  //  service with the longest matching prefix.
  def routes = Logger[IO](true, true)( Router[IO](
    "/status" -> statusRoute,
    "/api/cost/v1/workflow" -> workflowRoutes
  ).orNotFound).mapF(handleException)

//  def apiRouter = Router[IO] (
//    "/cost/v1/workflow" -> workflowRoutes //authorize(workflowRoutes)
//  )

  def statusRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok( IO { statusService.status() } )
    case _ -> Root =>
      // The default route result is NotFound. Sometimes MethodNotAllowed is more appropriate.
      MethodNotAllowed(Allow(GET))
  }

  val token = ""

  def workflowRoutes = HttpRoutes.of[IO] {
    case GET -> Root / workflowId =>
      logger.info("WE'RE HERE")
      Ok(IO { workflowCostService.getWorkflowCost(token, WorkflowId(workflowId)) })
    case _ -> Root =>
      MethodNotAllowed(Allow(GET))
  }


//  // ToDo: Move the auth stuff to an object
//  val authUser: Kleisli[OptionT[IO, ?], Request[IO], UserInfo] =
//    Kleisli(req => OptionT.liftF(IO {
//      val token = req.headers.get(CaseInsensitiveString("authorization")).getOrElse(throw HammException(401, "can't find auth header")).value.split(" ").last // ToDo: fix this
//      val samResponse = samDAO.getUserStatus(token)
//      UserInfo(samResponse, token)
//    }))
//
//  val authorize: AuthMiddleware[IO, UserInfo] = AuthMiddleware(authUser)
//



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