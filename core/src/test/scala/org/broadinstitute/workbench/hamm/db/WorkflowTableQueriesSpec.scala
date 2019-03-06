package org.broadinstitute.workbench.hamm.db

import java.time.Instant

import org.broadinstitute.workbench.hamm.model.{WorkflowCollectionId, WorkflowId}
import org.broadinstitute.workbench.hamm.TestComponent
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class WorkflowTableQueriesSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  val workflowId = WorkflowId("fake-id")
  val workflow = Workflow(workflowId, None, None, WorkflowCollectionId("fake-wf-collection-id"), false, Instant.now(), Instant.now(), Map.empty[String,String], 1)

  it should "insert and get a workflow" in { implicit session =>

    WorkflowTableQueries.getWorkflowQuery(workflowId) shouldBe None

    WorkflowTableQueries.insertWorkflowQuery(workflow)

    WorkflowTableQueries.getWorkflowQuery(workflowId) shouldBe Some(workflow)
    WorkflowTableQueries.getWorkflowCostQuery(workflowId) shouldBe Some(1)
  }


}