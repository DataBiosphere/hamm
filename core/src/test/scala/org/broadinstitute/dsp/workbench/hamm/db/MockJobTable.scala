package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.model.WorkflowCollectionId
import scalikejdbc.DBSession

import scala.collection.mutable

class MockJobTable(workflowTable: MockWorkflowTable) extends JobTableQueries {

  val jobs: mutable.Set[Job] = mutable.Set()


  def insertJobQuery(job: Job)(implicit session: DBSession) = {
    jobs += job
    jobs.size
  }

  def getJobQuery(jobUniquekey: JobUniqueKey)(implicit session: DBSession): Option[Job] = {
    jobs.find(job =>
      job.attempt.equals(jobUniquekey.attempt) &&
      job.callFqn.equals(jobUniquekey.callFqn) &&
      job.jobIndex.equals(jobUniquekey.jobIndex) &&
      job.workflowId.equals(jobUniquekey.workflowId)
    )
  }

  def getJobCostQuery(jobId: CallFqn)(implicit session: DBSession): Option[Double] = {
    jobs.find(job => job.callFqn.equals(jobId)).map(job => job.cost)
  }

  def getJobWorkflowCollectionIdQuery(jobId: CallFqn)(implicit session: DBSession): Option[WorkflowCollectionId] = {
    val workflowId = jobs.find(job => job.callFqn.equals(jobId)).map(job => job.workflowId).get
    workflowTable.getWorkflowQuery(workflowId).map(workflow => workflow.workflowCollectionId)
  }

}
