package org.broadinstitute.workbench.hamm
package db

import java.time.Instant
import cats.effect.Async
import cats.implicits._
import doobie._
import doobie.free.connection
import doobie.implicits._
import org.broadinstitute.workbench.hamm.db.WorkflowCostDAO._

class WorkflowCostDAO[F[_]: Async](transactor: Transactor[F]) {
  val createTable: F[Unit] = {
    val connIO = for {
      createTable <- createSql.run
      createIndex <- if(createTable == 0) createIndex.run else connection.raiseError(new Exception(s"creating table $workflowTableName failed with $createTable"))
      _ <- if(createIndex == 0) ().pure[ConnectionIO] else connection.raiseError(new Exception(s"creating index labelsIndex failed with $createIndex"))
    } yield ()

    connIO.transact(transactor)
  }

  def insert(workflowddb: WorkflowDB): F[Int] = insertWorkflowSql(workflowddb).run.transact[F](transactor)

  def getWorkflowDB(workflowId: WorkflowId): F[WorkflowDB] = getWorkflowDBSql(workflowId).unique.transact[F](transactor)

  def getWorkflowCollectionId(workflowId: WorkflowId): F[WorkflowCollectionId] = getWorkflowCollectionIdSql(workflowId).unique.transact[F](transactor)

  def getWorkflowCostWithLabel(label: Label): F[Double] = getWorkflowCostSqlWithLabel(label).unique.transact[F](transactor)
}

object WorkflowCostDAO {
  def apply[F[_]: Async](transactor: Transactor[F]): WorkflowCostDAO[F] = new WorkflowCostDAO(transactor)

  val createSql: Update0 =
    (fr"CREATE TABLE IF NOT EXISTS" ++ workflowTableName ++
      fr"(id SERIAL NOT NULL," ++
      workflowIdFragment ++ fr"UUID NOT NULL," ++
      parentWorkflowIdFragment ++ fr"UUID," ++
      rootWorkflowIdFragment ++ fr"UUID," ++
      workflowCollectionIdFragment ++ fr"UUID NOT NULL," ++
      isSubWorkflowFragment ++ fr"BOOLEAN NOT NULL," ++
      startTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      endTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      labelsFragment ++ fr"JSONB," ++
      costFragment ++ fr"FLOAT8 NOT NULL" ++
      fr")").update

  val createIndex: Update0 = (fr"CREATE INDEX IF NOT EXISTS labelsIndex ON" ++ workflowTableName ++ fr"USING GIN (" ++ labelsFragment ++ fr")").update

  def insertWorkflowSql(workflowddb: WorkflowDB): Update0 = {
    val query =
      s"""INSERT INTO WORKFLOW_COST (
                $workflowIdFieldName,
                $parentWorkflowIdFieldName,
                $rootWorkflowIdFieldName,
                $workflowCollectionIdFieldName,
                $isSubWorkflowFieldName,
                $startTimeFieldName,
                $endTimeFieldName,
                $labelsFieldName,
                $costFieldName
        ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
      """

    Update[WorkflowDB](query).toUpdate0(workflowddb)
  }

  def getWorkflowDBSql(workflowId: WorkflowId): Query0[WorkflowDB] =
    (fr"select" ++
         workflowIdFragment ++ fr"," ++
         parentWorkflowIdFragment ++ fr"," ++
         rootWorkflowIdFragment ++ fr"," ++
         workflowCollectionIdFragment ++ fr"," ++
         isSubWorkflowFragment ++ fr"," ++
         startTimeFragment ++ fr"," ++
         endTimeFragment ++ fr"," ++
         labelsFragment ++ fr"," ++
         costFragment ++ fr"from WORKFLOW_COST where workflow_id = ${workflowId}")
      .query[WorkflowDB]

  def getWorkflowCostSql(workflowId: WorkflowId): Query0[WorkflowCost] =
    sql"""select
           workflow_id,
           cost
         from WORKFLOW_COST where workflow_id = ${workflowId}
       """
      .query[WorkflowCost]

  def getWorkflowCollectionIdSql(workflowId: WorkflowId): Query0[WorkflowCollectionId] =
    (fr"select" ++ workflowCollectionIdFragment ++ fr"from" ++ workflowTableName ++ fr"where workflow_id = ${workflowId}")
      .query[WorkflowCollectionId]

  def getWorkflowCostSqlWithLabel(label: Label): Query0[Double] =
    (fr"select sum(cost) from" ++ workflowTableName ++ fr"where" ++ labelsFragment ++ fr"->>${label.key}" ++ fr"=" ++ fr"${label.value}").query[Double]
}

final case class WorkflowDB(
    workflowId: WorkflowId,
    parentWorkflow: Option[WorkflowId],
    rootWorkflow: Option[WorkflowId],
    workflowCollectionId: WorkflowCollectionId,
    isSubWorkflow: Boolean,
    startTime: Instant,
    endTime: Instant,
    label: Map[String, String],
    cost: Double)

final case class Label(key: String, value: String)
final case class WorkflowCost(workflowId: WorkflowId, cost: Double)