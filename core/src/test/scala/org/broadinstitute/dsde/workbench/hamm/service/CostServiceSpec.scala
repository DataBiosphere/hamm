package org.broadinstitute.dsde.workbench.hamm.service

import org.broadinstitute.dsde.workbench.hamm.model.JobId
import org.broadinstitute.dsde.workbench.hamm.{HammLogger, TestComponent, TestData}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

class CostServiceSpec extends FlatSpec with Matchers with TestComponent with HammLogger with BeforeAndAfterAll with BeforeAndAfterEach {

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



  it should "get the cost of a workflow" in {
    val result = costService.getWorkflowCost(TestData.testToken, TestData.testWorkflowId)
    result shouldBe WorkflowCostResponse(TestData.testWorkflowId, TestData.testWorkflow.cost)

  }


  it should "get the cost of a job" in {
    val result = costService.getJobCost(TestData.testToken, JobId(TestData.testJob.callFqn.asString))
    result shouldBe JobCostResponse(JobId(TestData.testJob.callFqn.asString), TestData.testWorkflow.cost)
  }


}
