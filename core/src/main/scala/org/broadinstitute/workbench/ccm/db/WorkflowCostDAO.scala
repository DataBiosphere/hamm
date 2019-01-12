package org.broadinstitute.workbench.ccm
package db

import java.time.Instant

import cats.effect.Async
import cats._, cats.data._, cats.implicits._
import doobie._, doobie.implicits._
import org.broadinstitute.workbench.ccm.db.WorkflowCostDAO._

class WorkflowCostDAO[F[_]: Async](transactor: Transactor[F]) {
  val createTable = create.run.transact[F](transactor)

  def insert(workflowddb: WorkflowDB): F[Int] = insertConnIO(workflowddb).run.transact[F](transactor)

  def getWorkflowDB(workflowId: WorkflowId): F[WorkflowDB] = getWorkflowDBConnIO(workflowId).unique.transact[F](transactor)
}

object WorkflowCostDAO{
  def apply[F[_]: Async](transactor: Transactor[F]): WorkflowCostDAO[F] = new WorkflowCostDAO(transactor)

  val tableName = "WORKFLOW_COST"

  val workflowIdFieldName = "workflow_id"
  val endTimeFieldName = "end_time"
  val labelNameFieldName = "label_name"
  val labelValueFieldName = "label_value"
  val costFieldName = "cost"

  val create: Update0 =
    sql"""CREATE TABLE IF NOT EXISTS WORKFLOW_COST (
                 id SERIAL,
                 workflow_id UUID NOT NULL,
                 end_time TIMESTAMPTZ NOT NULL,
                 label_name VARCHAR,
                 label_value VARCHAR,
                 cost FLOAT8 NOT NULL
               )
       """.update

  def insertConnIO(workflowddb: WorkflowDB): Update0 = {
    val query = """INSERT INTO WORKFLOW_COST (
          workflow_id,
          end_time,
          label_name,
          label_value,
          cost
        ) values (
          ?,
          ?,
          ?,
          ?,
          ?
        )
      """
    Update[WorkflowDB](query).toUpdate0(workflowddb)
  }

  def getWorkflowDBConnIO(workflowId: WorkflowId): Query0[WorkflowDB] =
    sql"""select
           workflow_id,
           end_time,
           label_name,
           label_value,
           cost
         from WORKFLOW_COST where workflow_id = ${workflowId}
       """
    .query[WorkflowDB](workflowDBRead)
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

final case class WorkflowDB(workflowId: WorkflowId, endTime: Instant, label: Option[Label], cost: Double)