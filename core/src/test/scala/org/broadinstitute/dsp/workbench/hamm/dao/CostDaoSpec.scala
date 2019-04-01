package org.broadinstitute.dsp.workbench.hamm

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

class CostDaoSpec extends FlatSpec with Matchers with TestComponent with HammLogger with BeforeAndAfterAll with BeforeAndAfterEach {

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
    val result = costDbDao.getWorkflowCost(TestData.testToken, TestData.testWorkflowId)
    result shouldBe WorkflowCostResponse(TestData.testWorkflowId, TestData.testWorkflow.cost)

  }


  it should "get the cost of a job" in {
    val result = costDbDao.getJobCost(TestData.testToken, TestData.testWorkflowId, TestData.testCallFqn, TestData.testAttempt, TestData.testJobIndex)
    result shouldBe JobCostResponse(TestData.testWorkflowId, TestData.testCallFqn, TestData.testAttempt, TestData.testJobIndex, TestData.testWorkflow.cost)
  }
}
