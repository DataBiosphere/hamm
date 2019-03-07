package org.broadinstitute.workbench.hamm.db

import org.broadinstitute.workbench.hamm.{TestComponent, TestData}
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class WorkflowTableQueriesSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  it should "insert and get a workflow" in { implicit session =>

    WorkflowTableQueries.getWorkflowQuery(TestData.testWorkflowId) shouldBe None

    WorkflowTableQueries.insertWorkflowQuery(TestData.testWorkflow)

    WorkflowTableQueries.getWorkflowQuery(TestData.testWorkflowId) shouldBe Some(TestData.testWorkflow)
    WorkflowTableQueries.getWorkflowCostQuery(TestData.testWorkflowId) shouldBe Some(1)
  }


}