package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class WorkflowTableSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {
  it should "insert and get a workflow" in { implicit session =>
    WorkflowTable.getWorkflowQuery(TestData.testWorkflowId) shouldBe None

    WorkflowTable.insertWorkflowQuery(TestData.testWorkflow)

    WorkflowTable.getWorkflowQuery(TestData.testWorkflowId) shouldBe Some(TestData.testWorkflow)
    WorkflowTable.getWorkflowCostQuery(TestData.testWorkflowId) shouldBe Some(1)
  }
}