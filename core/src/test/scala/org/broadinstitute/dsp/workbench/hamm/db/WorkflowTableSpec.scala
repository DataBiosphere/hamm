package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class WorkflowTableSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  it should "insert and get a workflow" in { implicit session =>
    workflowTable.getWorkflowQuery(TestData.testWorkflowId) shouldBe None

    workflowTable.insertWorkflowQuery(TestData.testWorkflow)

    workflowTable.getWorkflowQuery(TestData.testWorkflowId) shouldBe Some(TestData.testWorkflow)
    workflowTable.getWorkflowCostQuery(TestData.testWorkflowId) shouldBe Some(1)
  }


}