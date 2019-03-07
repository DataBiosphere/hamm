package org.broadinstitute.dsde.workbench.hamm.api

import cats.effect.IO
import org.broadinstitute.dsde.workbench.hamm.{HammLogger, TestComponent, TestData}
import org.broadinstitute.dsde.workbench.hamm.db.WorkflowTableQueries
import org.broadinstitute.dsde.workbench.hamm.service.WorkflowCostResponse
import org.http4s.circe.CirceEntityDecoder._
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback
import io.circe.generic.auto._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.headers.Authorization

class HammRoutesSpec extends FlatSpec with Matchers with TestComponent with AutoRollback with Http4sDsl[IO] with HammLogger {


  // Return true if match succeeds; otherwise false
  def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }


  it should "get status" in { _ =>

    val response = hammRoutes.routes.apply {
      Request(method = Method.GET, uri = Uri.uri("/status"))
    }

    check(response, Status.Ok, Some(()))
  }


  it should "get a workflow's cost" in { implicit session =>
    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe false
    samAuthProvider.samClient.actionsPerResourcePerToken += (TestData.testSamResource, TestData.testToken) -> Set(TestData.testSamResourceAction)

    val res1 = WorkflowTableQueries.getWorkflowQuery(TestData.testWorkflowId) //shouldBe None
    val res2 = WorkflowTableQueries.insertWorkflowQuery(TestData.testWorkflow)//
    val res3 = WorkflowTableQueries.getWorkflowQuery(TestData.testWorkflowId) //shouldBe Some(TestData.testWorkflow)

    val uri = "/api/cost/v1/workflow/" + TestData.testWorkflowId.id

    val response = hammRoutes.routes.apply {
     Request(
       method = Method.GET,
       uri = Uri.unsafeFromString(uri),
       headers = Headers(Authorization(TestData.testToken)))
    }

    check(response, Status.Ok, Some(WorkflowCostResponse(TestData.testWorkflowId, TestData.testWorkflow.cost)))
  }

}
