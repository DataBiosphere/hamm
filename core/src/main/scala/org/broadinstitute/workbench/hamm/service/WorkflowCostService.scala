package org.broadinstitute.workbench.hamm.service

import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.db.{DbReference, JobTableQueries, WorkflowTableQueries}
import org.broadinstitute.workbench.hamm.model._
import org.broadinstitute.workbench.hamm.model.HammException
import org.http4s.Status

class WorkflowCostService(pricing: GooglePriceListDAO,
                          workflowDAO: WorkflowMetadataDAO,
                          samAuthProvider: SamAuthProvider,
                          dbRef: DbReference) extends HammLogger {

  def getWorkflowCost(token: String, workflowId: WorkflowId): WorkflowCostResponse = {
    dbRef.inReadOnlyTransaction { implicit session =>
      WorkflowTableQueries.getWorkflowSql(workflowId)
    } match {
      case Some(workflow) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflow.workflowCollectionId.asString)) =>
        WorkflowCostResponse(workflowId, workflow.cost)
      // this exception occurs when the workflow isn't one we have a cost for OR if the user doesn't have permissions on the collection.
      case None => throw HammException(Status.NotFound.code, s"Workflow $workflowId was either not found.")
    }
  }

  def getJobCost(token: String, jobId: JobId): JobCostResponse = {
    val jobCostNotFoundException = HammException(Status.NotFound.code, s"Cost for job $jobId was not found.")

    dbRef.inReadOnlyTransaction { implicit session =>
      JobTableQueries.getJobWorkflowCollectionId(jobId) match {
        case Some(workflowCollectionId) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflowCollectionId.asString)) => {
          val jobCost = JobTableQueries.getJobCostSql(jobId).getOrElse(throw jobCostNotFoundException)
          JobCostResponse(jobId, jobCost)
        }
        case None => throw jobCostNotFoundException
      }
    }
  }
}

final case class WorkflowCostResponse(workflowId: WorkflowId, cost: Double)
final case class JobCostResponse(jobId: JobId, cost: Double)