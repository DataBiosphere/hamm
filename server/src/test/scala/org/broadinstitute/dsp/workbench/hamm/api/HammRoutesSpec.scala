package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers, Assertion}


class HammRoutesSpec extends FlatSpec with Matchers with TestComponent with Http4sDsl[IO] with HammLogger with BeforeAndAfterAll with BeforeAndAfterEach {

  val hammRoutes = new HammRoutes(samAuthProvider, CostService[IO](costDbDao), StatusService[IO], VersionService[IO])

  override def beforeAll() = {
    samAuthProvider.samClient.actionsPerResourcePerToken += (TestData.testSamResource, TestData.testToken) -> Set(TestData.testSamResourceAction)
  }

  override def afterAll() = {
    samAuthProvider.samClient.actionsPerResourcePerToken.remove((TestData.testSamResource, TestData.testToken))
    ()
  }

  override def beforeEach() = {
    mockWorkflowTable.workflows += TestData.testWorkflow
    mockJobTable.jobs += TestData.testJob
  }

  override def afterEach() = {
    mockWorkflowTable.workflows.remove(TestData.testWorkflow)
    mockJobTable.jobs.remove(TestData.testJob)
    ()
  }


  // Return true if match succeeds; otherwise false
  def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Assertion =  {
    val actualResp         = actual.unsafeRunSync

    actualResp.status shouldBe expectedStatus

    expectedBody.fold[Assertion](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty shouldBe(true))( // Verify Response's body is empty.
      expected => actualResp.as[A].unsafeRunSync shouldBe expected
    )
  }


  it should "get status" in {
    val response = hammRoutes.routes.apply {
      Request(method = Method.GET, uri = Uri.uri("/status"))
    }
    response.unsafeRunSync().status shouldBe Status.Ok
  }

  it should "get version" in {
    val response = hammRoutes.routes.apply {
      Request(method = Method.GET, uri = Uri.uri("/version"))
    }
    response.unsafeRunSync().status shouldBe Status.Ok
  }

  it should "get a workflow's cost" in {
    val uri = "/api/cost/v1/workflow/" + TestData.testWorkflow.workflowId.id

    val response = hammRoutes.routes.apply {
      Request(
        method = Method.GET,
        uri = Uri.unsafeFromString(uri),
        headers = Headers(Authorization(TestData.testToken)))
    }

    check(response, Status.Ok, Some(WorkflowCostResponse(TestData.testWorkflowId, TestData.testWorkflow.cost)))
  }

  it should "get a job's cost" in {
    val uri = "/api/cost/v1/job/" + TestData.testJob.workflowId.id + "/" + TestData.testJob.callFqn.asString + "/" + TestData.testJob.attempt.toString + "/" + TestData.testJob.jobIndex.toString

    val response = hammRoutes.routes.apply {
      Request(
        method = Method.GET,
        uri = Uri.unsafeFromString(uri),
        headers = Headers(Authorization(TestData.testToken)))
    }

    //This will change based on changing Job cost table, just doing it weird for now
    check(response, Status.Ok, Some(JobCostResponse(TestData.testWorkflowId, TestData.testCallFqn, TestData.testAttempt, TestData.testJobIndex, TestData.testJob.cost)))
  }

}
