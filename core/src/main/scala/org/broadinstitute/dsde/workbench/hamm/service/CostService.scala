package org.broadinstitute.dsde.workbench.hamm.service

import org.broadinstitute.dsde.workbench.hamm.HammLogger
import org.broadinstitute.dsde.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.dsde.workbench.hamm.db.{CallFqn, DbReference, JobTableQueries, WorkflowTableQueries}
import org.broadinstitute.dsde.workbench.hamm.model._
import org.broadinstitute.dsde.workbench.hamm.model.HammException
import org.http4s.Credentials.Token
import org.http4s.Status

class CostService(samAuthProvider: SamAuthProvider, dbRef: DbReference) extends HammLogger {

  def getWorkflowCost(token: Token, workflowId: WorkflowId): WorkflowCostResponse = {
    dbRef.inReadOnlyTransaction { implicit session =>
      WorkflowTableQueries.getWorkflowQuery(workflowId)
    } match {
      case Some(workflow) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflow.workflowCollectionId.asString)) =>
        WorkflowCostResponse(workflowId, workflow.cost)
      // this exception occurs when the workflow isn't one we have a cost for OR if the user doesn't have permissions on the collection.
      case None => throw HammException(Status.NotFound.code, s"Cost for Workflow ${workflowId.id} was not found.")
    }
  }

  def getJobCost(token: Token, jobId: JobId): JobCostResponse = {
    val jobCostNotFoundException = HammException(Status.NotFound.code, s"Cost for job ${jobId.id} was not found.")

    dbRef.inReadOnlyTransaction { implicit session =>

      JobTableQueries.getJobWorkflowCollectionIdQuery(CallFqn(jobId.id)) match { // doing this weird CallFqn thing in my PR for now until I understand what these things mean...
        case Some(workflowCollectionId) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflowCollectionId.asString)) => {
          val jobCost = JobTableQueries.getJobCostQuery(CallFqn(jobId.id)).getOrElse(throw jobCostNotFoundException)
          JobCostResponse(jobId, jobCost)
        }
        case None => throw jobCostNotFoundException
      }
    }
  }
}

final case class WorkflowCostResponse(workflowId: WorkflowId, cost: Double)
final case class JobCostResponse(jobId: JobId, cost: Double)
