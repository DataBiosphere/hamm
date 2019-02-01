package org.broadinstitute.workbench.hamm.db

import java.time.Instant

import cats.implicits._
import cats.effect.Async
import doobie._
import doobie.implicits._
import org.broadinstitute.workbench.hamm.WorkflowId
import JobCostDAO._
import doobie.free.connection

class JobCostDAO[F[_]: Async](transactor: Transactor[F]) {
  val createTable: F[Unit] = {
    val connIO = for {
      createTable <- createSql.run
      createIndex <- if(createTable == 0) createUniqueIndexSql.run else connection.raiseError(new Exception(s"creating table $jobTableName failed with $createTable"))
      _ <- if(createIndex == 0) ().pure[ConnectionIO] else connection.raiseError(new Exception(s"creating index callUniqueIdentifierIndex failed with $createIndex"))
    } yield ()

    connIO.transact(transactor)
  }

  def insert(callCost: JobCost): F[Int] = insertCallCostSql(callCost).run.transact[F](transactor)

  def getJobCost(callUniquekey: CallUniquekey): F[JobCost] = getCallCostSql(callUniquekey).unique.transact[F](transactor)
}

object JobCostDAO {
  def apply[F[_]: Async](transactor: Transactor[F]): JobCostDAO[F] = new JobCostDAO(transactor)

  val createSql: Update0 =
    (fr"CREATE TABLE IF NOT EXISTS" ++ jobTableName ++
      fr"(id SERIAL," ++
      workflowIdFragment ++ fr"UUID NOT NULL," ++
      callFqnFragment ++ fr"VARCHAR(255) NOT NULL," ++
      attemptFragment ++ fr"SMALLINT NOT NULL," ++
      jobIndexFragment ++ fr"INTEGER NOT NULL," ++
      vendorJobIdFragment ++ fr"VARCHAR(255)," ++
      startTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      endTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      costFragment ++ fr"FLOAT8 NOT NULL" ++
      fr")").update

  val createUniqueIndexSql: Update0 = (fr"CREATE UNIQUE INDEX IF NOT EXISTS callUniqueIdentifierIndex ON" ++ jobTableName ++ fr"(" ++
    workflowIdFragment ++ fr"," ++
    callFqnFragment ++ fr"," ++
    attemptFragment ++ fr"," ++
    vendorJobIdFragment ++
    fr")").update

  def insertCallCostSql(callCost: JobCost): Update0 = {
    val query =
      s"""INSERT INTO JOB_COST (
                $workflowIdFieldName,
                $callFqnFieldName,
                $attemptFieldName,
                $jobIndexFieldName,
                $vendorJobIdFieldName,
                $startTimeFieldName,
                $endTimeFieldName,
                $costFieldName
        ) values (?, ?, ?, ?, ?, ?, ?, ?)
      """

    Update[JobCost](query).toUpdate0(callCost)
  }

  def getCallCostSql(callUniquekey: CallUniquekey): Query0[JobCost] =
    (fr"select" ++
      workflowIdFragment ++ fr"," ++
      callFqnFragment ++ fr"," ++
      attemptFragment ++ fr"," ++
      jobIndexFragment ++ fr"," ++
      vendorJobIdFragment ++ fr"," ++
      startTimeFragment ++ fr"," ++
      endTimeFragment ++ fr"," ++
      costFragment ++
      fr"FROM JOB_COST WHERE WORKFLOW_ID = ${callUniquekey.workflowId} AND" ++
      callFqnFragment ++ fr"=${callUniquekey.callFqn} AND" ++
      attemptFragment ++ fr"=${callUniquekey.attempt}").query[JobCost]
}

final case class CallFqn(asString: String) extends AnyVal
final case class CallUniquekey(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndexId: Int)
final case class JobCost(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndexId: Int, gcpJobId: Option[String], startTime: Instant, endTime: Instant, cost: Double){
  val uniqueKey = CallUniquekey(workflowId, callFqn, attempt, jobIndexId)
}
