package org.broadinstitute.workbench.ccm
package db

import java.time.Instant
import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.Async
import doobie._
import doobie.implicits._
import org.broadinstitute.workbench.ccm.db.WorkflowCostDAO._

class WorkflowCostDAO[F[_]: Async](transactor: Transactor[F]) {
  val createTable = createSql.run.transact[F](transactor)

  def insert(workflowddb: WorkflowDB): F[Int] = insertWorkflowSql(workflowddb).run.transact[F](transactor)

  def getWorkflowDB(workflowId: WorkflowId): F[WorkflowCost] = getWorkflowCostSql(workflowId).unique.transact[F](transactor)
}

object WorkflowCostDAO {
  def apply[F[_]: Async](transactor: Transactor[F]): WorkflowCostDAO[F] = new WorkflowCostDAO(transactor)

  val createSql: Update0 =
    (fr"CREATE TABLE IF NOT EXISTS" ++ workflowTableName ++
      fr"(id SERIAL," ++
      workflowIdFragment ++ fr"UUID NOT NULL," ++
      subWorkflowIdFragment ++ fr"UUID[]," ++
      isSubWorkflowFragment ++ fr"BOOLEAN NOT NULL," ++
      submissionIdFragment ++ fr"UUID NOT NULL," ++
      workspaceIdFragment ++ fr"UUID NOT NULL," ++
      billingProjectIdFragment ++ fr"UUID NOT NULL," ++
      startTimeFragment ++ fr"TIMESTAMPTZ," ++
      endTimeFragment ++ fr"TIMESTAMPTZ," ++
      labelNameFragment ++ fr"VARCHAR," ++
      labelValueFragment ++ fr"VARCHAR," ++
      costFragment ++ fr"FLOAT8 NOT NULL" ++
      fr")").update

  def insertWorkflowSql(workflowddb: WorkflowDB): Update0 = {
    val query =
      s"""INSERT INTO WORKFLOW_COST (
                $workflowIdFieldName,
                $subWorkflowIdFieldName,
                $isSubWorkflowFieldName,
                $submissionIdFieldName,
                $workspaceIdFieldName,
                $billingProjectIdFieldName,
                $startTimeFieldName,
                $endTimeFieldName,
                $labelNameFieldName,
                $labelValueFieldName,
                $costFieldName
        ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      """

    Update[WorkflowDB](query).toUpdate0(workflowddb)
  }

  def getWorkflowCostSql(workflowId: WorkflowId): Query0[WorkflowCost] =
    sql"""select
           workflow_id,
           cost
         from WORKFLOW_COST where workflow_id = ${workflowId}
       """
      .query[WorkflowCost]
}

sealed abstract class Label {
  def name: String
  def labelValue: String
}

object Label {

  final case class Submission(labelValue: String) extends Label {
    def name = "submission"
  }

  final case class Workspace(labelValue: String) extends Label {
    def name = "workspace"
  }

  val validNames = List("submission", "workspace")
}

final case class WorkflowDB(
    workflowId: WorkflowId,
    subWorkflows: Option[NonEmptyList[WorkflowId]],
    isSubWorkflow: Boolean,
    submissionId: SubmissionId,
    workspaceId: WorkspaceId,
    billingProjectId: UUID, //TODO: fix the type
    startTime: Instant,
    endTime: Instant,
    label: Option[Label],
    cost: Double)

final case class WorkflowCost(workflowId: WorkflowId, cost: Double)