package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class JobTableQueriesSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {


  it should "insert and get a job" in { implicit session =>
    jobTable.getJobQuery(TestData.testJobUniqueKey) shouldBe None

    workflowTable.insertWorkflowQuery(TestData.testWorkflow)
    jobTable.insertJobQuery(TestData.testJob)

    jobTable.getJobQuery(TestData.testJobUniqueKey) shouldBe Some(TestData.testJob)
    jobTable.getJobCostQuery(TestData.testCallName) shouldBe Some(1)
    jobTable.getJobWorkflowCollectionIdQuery(TestData.testCallName) shouldBe Some(TestData.testWorkflowCollectionId)
  }



}
