package org.broadinstitute.workbench.ccm.db

import java.time.Instant

import cats.effect.Async
import doobie._
import doobie.implicits._
import org.broadinstitute.workbench.ccm.WorkflowId
import JobCostDAO._

class JobCostDAO[F[_]: Async](transactor: Transactor[F]) {
  val createTable = createSql.run.transact[F](transactor)

  def insert(callCost: CallCost): F[Int] = insertCallCostSql(callCost).run.transact[F](transactor)

  def getCallCost(callUniquekey: CallUniquekey): F[CallCost] = getCallCostSql(callUniquekey).unique.transact[F](transactor)

}

object JobCostDAO {
  def apply[F[_]: Async](transactor: Transactor[F]): JobCostDAO[F] = new JobCostDAO(transactor)

  val createSql: Update0 =
    (fr"CREATE TABLE IF NOT EXISTS" ++ jobTableName ++
      fr"(id SERIAL," ++
      workflowIdFragment ++ fr"UUID NOT NULL," ++
      callFqnFragment ++ fr"VARCHAR(255) NOT NULL," ++
      attemptFragment ++ fr"SMALLINT NOT NULL," ++
      gcpJobIdFragment ++ fr"VARCHAR(255)," ++
      startTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      endTimeFragment ++ fr"TIMESTAMPTZ NOT NULL," ++
      costFragment ++ fr"FLOAT8 NOT NULL" ++
      fr")").update

  def insertCallCostSql(callCost: CallCost): Update0 = {
    val query =
      s"""INSERT INTO JOB_COST (
                $workflowIdFieldName,
                $callFqnFieldName,
                $attemptFieldName,
                $gcpJobIdFieldName,
                $startTimeFieldName,
                $endTimeFieldName,
                $costFieldName
        ) values (?, ?, ?, ?, ?, ?, ?)
      """

    Update[CallCost](query).toUpdate0(callCost)
  }

  def getCallCostSql(callUniquekey: CallUniquekey): Query0[CallCost] =
    (fr"select" ++
      workflowIdFragment ++ fr"," ++
      callFqnFragment ++ fr"," ++
      attemptFragment ++ fr"," ++
      gcpJobIdFragment ++ fr"," ++
      startTimeFragment ++ fr"," ++
      endTimeFragment ++ fr"," ++
      costFragment ++
      fr"FROM JOB_COST WHERE WORKFLOW_ID = ${callUniquekey.workflowId} AND" ++
      callFqnFragment ++ fr"=${callUniquekey.callFqn} AND" ++
      attemptFragment ++ fr"=${callUniquekey.attempt}").query[CallCost]
}

final case class CallFqn(asString: String) extends AnyVal
final case class CallUniquekey(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short)
final case class CallCost(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, gcpJobId: Option[String], startTime: Instant, endTime: Instant, cost: Double)
