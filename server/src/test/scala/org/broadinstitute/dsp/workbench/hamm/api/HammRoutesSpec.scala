package org.broadinstitute.dsp.workbench.hamm.api

import cats.effect.IO
import io.circe.generic.auto._
import org.broadinstitute.dsp.workbench.hamm.{HammLogger, TestData}
import org.broadinstitute.dsp.workbench.hamm.model.JobId
import org.broadinstitute.dsp.workbench.hamm.service.{JobCostResponse, WorkflowCostResponse}
import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}


class HammRoutesSpec extends FlatSpec with Matchers with TestComponent with Http4sDsl[IO] with HammLogger with BeforeAndAfterAll with BeforeAndAfterEach {

  val hammRoutes = new HammRoutes(samAuthProvider, costService, statusService)

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
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }


  it should "get status" in {

    val response = hammRoutes.routes.apply {
      Request(method = Method.GET, uri = Uri.uri("/status"))
    }
    check(response, Status.Ok, Some(()))
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
    val uri = "/api/cost/v1/job/" + TestData.testJob.callFqn.asString

    val response = hammRoutes.routes.apply {
      Request(
        method = Method.GET,
        uri = Uri.unsafeFromString(uri),
        headers = Headers(Authorization(TestData.testToken)))
    }

    //This will change based on changing Job cost table, just doing it weird for now
    check(response, Status.Ok, Some(JobCostResponse(JobId(TestData.testJob.callFqn.asString), TestData.testJob.cost)))
  }

}
