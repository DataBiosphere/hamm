package org.broadinstitute.workbench.hamm.db

import java.time.Instant

import org.broadinstitute.workbench.hamm.model.{WorkflowCollectionId, WorkflowId}
import org.broadinstitute.workbench.hamm.TestComponent
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class JobTableQueriesSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  val workflowId = WorkflowId("fake-id")
  val callFqn = CallFqn("fake-call-fqn")
  val attempt = 2.toShort
  val jobIndex = 3
  val jobUniqueKey = JobUniqueKey(workflowId, callFqn, 2, 3)
  val workflowCollectionId = WorkflowCollectionId("fake-wf-collection-id")
  val job = new Job(workflowId, callFqn, attempt, jobIndex, Some("fake-vendor-id"), Instant.now(), Instant.now(), 1)
  val workflow = Workflow(workflowId, None, None, workflowCollectionId, false, Instant.now(), Instant.now(), Map.empty[String,String], 1)




  it should "insert and get a job" in { implicit session =>
    JobTableQueries.getJobQuery(jobUniqueKey) shouldBe None

    WorkflowTableQueries.insertWorkflowQuery(workflow)

    JobTableQueries.insertJobQuery(job)

    JobTableQueries.getJobQuery(jobUniqueKey) shouldBe Some(job)
    JobTableQueries.getJobCostQuery(callFqn) shouldBe Some(1)
    JobTableQueries.getJobWorkflowCollectionIdQuery(callFqn) shouldBe Some(workflowCollectionId)
  }



}
