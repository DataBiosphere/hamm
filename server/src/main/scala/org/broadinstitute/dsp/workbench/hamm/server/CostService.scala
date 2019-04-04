package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.Sync
import io.circe.generic.auto._
import org.broadinstitute.dsp.workbench.hamm.db._
import org.broadinstitute.dsp.workbench.hamm.model.{HammException, SamResource, WorkflowId}
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamAuthProvider
import org.http4s.{AuthedService, Status}
import org.http4s.Credentials.Token
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl

class CostService[F[_]: Sync](samAuthProvider: SamAuthProvider, dbRef: DbReference, jobTable: JobTableQueries, workflowTable: WorkflowTableQueries) extends Http4sDsl[F] with HammLogger {
  val service: AuthedService[Token, F] = AuthedService.apply{
    case GET -> Root / "workflow" / workflowId as userToken =>
      Ok(Sync[F].delay { getWorkflowCost(userToken, WorkflowId(workflowId)) })
    case GET -> Root / "job" / workflowId / callFqn / attempt / IntVar(jobIndex)  as userToken =>
      Ok(Sync[F].delay { getJobCost(userToken, WorkflowId(workflowId), CallFqn(callFqn), attempt.toShort, jobIndex) })
  }

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

  def getJobCost(token: Token, workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndex: Int ): JobCostResponse = {
    val jobCostNotFoundException = HammException(Status.NotFound.code, s"Cost for job ${callFqn.asString}, attempt ${attempt.toString} in workflow ${workflowId.id} for job index ${jobIndex.toString} was not found.")
    val jobUniqueKey =  JobUniqueKey(workflowId, callFqn, attempt, jobIndex)
    dbRef.inReadOnlyTransaction { implicit session =>

      jobTable.getJobWorkflowCollectionIdQuery(jobUniqueKey) match {
        case Some(workflowCollectionId) if samAuthProvider.hasWorkflowCollectionPermission(token, SamResource(workflowCollectionId.asString)) => {
          val jobCost = jobTable.getJobCostQuery(jobUniqueKey).getOrElse(throw jobCostNotFoundException)
          JobCostResponse(workflowId, callFqn, attempt, jobIndex, jobCost)
        }
        case None => throw jobCostNotFoundException
      }
    }
  }
}

object CostService {
  def apply[F[_]: Sync](samAuthProvider: SamAuthProvider, dbRef: DbReference, jobTable: JobTableQueries, workflowTable: WorkflowTableQueries): CostService[F] = new CostService[F](samAuthProvider, dbRef, jobTable, workflowTable)
}

final case class WorkflowCostResponse(workflowId: WorkflowId, cost: Double)
final case class JobCostResponse(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndex: Int, cost: Double)