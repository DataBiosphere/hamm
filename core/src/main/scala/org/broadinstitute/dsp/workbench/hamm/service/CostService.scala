package org.broadinstitute.dsp.workbench.hamm.service

import org.broadinstitute.dsp.workbench.hamm.db._
import org.broadinstitute.dsp.workbench.hamm.model._
import org.broadinstitute.dsp.workbench.hamm.model.HammException
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import org.broadinstitute.dsp.workbench.hamm.auth.SamAuthProvider
import org.http4s.Credentials.Token
import org.http4s.Status

class CostService(samAuthProvider: SamAuthProvider, dbRef: DbReference, jobTable: JobTableQueries, workflowTable: WorkflowTableQueries) extends HammLogger {

  def getWorkflowCost(token: Token, workflowId: WorkflowId): WorkflowCostResponse = {
    dbRef.inReadOnlyTransaction { implicit session =>
      workflowTable.getWorkflowQuery(workflowId)
    } match {
      case Some(workflow) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflow.workflowCollectionId.asString)) =>
        WorkflowCostResponse(workflowId, workflow.cost)
      // this exception occurs when the workflow isn't one we have a cost for OR if the user doesn't have permissions on the collection.
      case Some(wf) => {
        logger.info("What " + wf.toString)
        throw HammException(Status.NotFound.code, s"Cost for Workflow ${workflowId.id} was not authorized.")
      }
      case None => throw HammException(Status.NotFound.code, s"Cost for Workflow ${workflowId.id} was not found.")
    }
  }

  def getJobCost(token: Token, jobId: JobId): JobCostResponse = {
    val jobCostNotFoundException = HammException(Status.NotFound.code, s"Cost for job ${jobId.id} was not found.")

    dbRef.inReadOnlyTransaction { implicit session =>

      jobTable.getJobWorkflowCollectionIdQuery(CallName(jobId.id)) match { // doing this weird CallName thing in my PR for now until I understand what these things mean...
        case Some(workflowCollectionId) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflowCollectionId.asString)) => {
          val jobCost = jobTable.getJobCostQuery(CallName(jobId.id)).getOrElse(throw jobCostNotFoundException)
          JobCostResponse(jobId, jobCost)
        }
        case None => throw jobCostNotFoundException
      }
    }
  }
}

final case class WorkflowCostResponse(workflowId: WorkflowId, cost: Double)
final case class JobCostResponse(jobId: JobId, cost: Double)
