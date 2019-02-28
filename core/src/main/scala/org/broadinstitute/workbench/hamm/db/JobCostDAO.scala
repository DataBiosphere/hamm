package org.broadinstitute.workbench.hamm.db

import java.sql.ResultSet
import java.time.Instant

import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.model._
import scalikejdbc._

class JobCostDAO extends HammLogger {
//  val createTable: F[Unit] = {
//    val connIO = for {
//      createTable <- createSql.run
//      createIndex <- if(createTable == 0) createUniqueIndexSql.run else connection.raiseError(new Exception(s"creating table $jobTableName failed with $createTable"))
//      _ <- if(createIndex == 0) ().pure[ConnectionIO] else connection.raiseError(new Exception(s"creating index callUniqueIdentifierIndex failed with $createIndex"))
//    } yield ()
//
//    connIO.transact(transactor)
//  }

//  def insert(callCost: JobCost): F[Int] = insertCallCostSql(callCost).run.transact[F](transactor)
//
//  def getJobCost(callUniquekey: CallUniquekey): F[JobCost] = getCallCostSql(callUniquekey).unique.transact[F](transactor)
}

object JobCostDAO {
  //def apply[F[_]: Async]/*(transactor: Transactor[F])*/: JobCostDAO[F] = new JobCostDAO()

  def insertCallCostSql(callCost: JobCost)(implicit session: DBSession) = {
    import Binders._
    val column = JobCost.column
    withSQL {
      insert.into(JobCost).namedValues(
        column.workflowId -> callCost.workflowId,
        column.callFqn -> callCost.callFqn,
        column.attempt -> callCost.attempt,
        column.jobIndex -> callCost.jobIndex,
        column.vendorJobId -> callCost.vendorJobId,
        column.startTime -> callCost.startTime,
        column.endTime -> callCost.endTime,
        column.cost -> callCost.cost)
    }.update.apply()
  }

  def getCallCostSql(callUniquekey: CallUniquekey)(implicit session: DBSession): Option[JobCost] = {
    import Binders._
    val e = JobCost.syntax("e")
    withSQL {
      select.from(JobCost as e)
        .where.eq(e.workflowId, callUniquekey.workflowId)
        .and.eq(e.callFqn, callUniquekey.callFqn)
        .and.eq(e.attempt, callUniquekey.attempt)
    }.map(JobCost(e.resultName)).single().apply()
  }


}

final case class CallFqn(asString: String) extends AnyVal
final case class CallUniquekey(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndexId: Int)
final case class JobCost(workflowId: WorkflowId, callFqn: CallFqn, attempt: Short, jobIndex: Int, vendorJobId: Option[String], startTime: Instant, endTime: Instant, cost: Double){
  val uniqueKey = CallUniquekey(workflowId, callFqn, attempt, jobIndex)
}

object JobCost extends SQLSyntaxSupport[JobCost] {
  override def tableName: String = "JOB_COST"
  import Binders._
  def apply(e: ResultName[JobCost])(rs: WrappedResultSet): JobCost = JobCost(
    rs.get(e.workflowId),
    rs.get(e.callFqn),
    rs.get(e.attempt),
    rs.get(e.jobIndex),
    rs.get(e.vendorJobId),
    rs.get(e.startTime),
    rs.get(e.endTime),
    rs.get(e.cost)
  )
}

object Binders {
  implicit val workflowIdTypeBinder: TypeBinder[WorkflowId] = new TypeBinder[WorkflowId] {
    def apply(rs: ResultSet, label: String): WorkflowId = WorkflowId(rs.getString(label))
    def apply(rs: ResultSet, index: Int): WorkflowId = WorkflowId(rs.getString(index))
  }

  implicit val callFqnTypeBinder: TypeBinder[CallFqn] = new TypeBinder[CallFqn] {
    def apply(rs: ResultSet, label: String): CallFqn = CallFqn(rs.getString(label))
    def apply(rs: ResultSet, index: Int): CallFqn = CallFqn(rs.getString(index))
  }

  implicit val workflowIdPbf: ParameterBinderFactory[WorkflowId] = ParameterBinderFactory[WorkflowId] {
    value => (stmt, idx) => stmt.setString(idx, value.id)
  }

  implicit val callFqnPbf: ParameterBinderFactory[CallFqn] = ParameterBinderFactory[CallFqn] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }
}