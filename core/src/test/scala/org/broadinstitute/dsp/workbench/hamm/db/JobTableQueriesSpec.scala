package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class JobTableQueriesSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {
  it should "insert and get a job" in { implicit session =>
    JobTable.getJobQuery(TestData.testJobUniqueKey) shouldBe None

    WorkflowTable.insertWorkflowQuery(TestData.testWorkflow)
    JobTable.insertJobQuery(TestData.testJob)

    JobTable.getJobQuery(TestData.testJobUniqueKey) shouldBe Some(TestData.testJob)
    JobTable.getJobCostQuery(TestData.testJobUniqueKey) shouldBe Some(1)
    JobTable.getJobWorkflowCollectionIdQuery(TestData.testJobUniqueKey) shouldBe Some(TestData.testWorkflowCollectionId)
  }
}
