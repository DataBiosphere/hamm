package org.broadinstitute.workbench.hamm.db

import java.sql.ResultSet
import java.time.Instant

import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.model._
import scalikejdbc._

object JobTableQueries extends HammLogger {

  def insertCallSql(callCost: Job)(implicit session: DBSession) = {
    import JobBinders._
    val column = Job.column
    withSQL {
      insert.into(Job).namedValues(
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

  def getCallSql(callUniquekey: CallUniquekey)(implicit session: DBSession): Option[Job] = {
    import JobBinders._
    val e = Job.syntax("e")
    withSQL {
      select.from(Job as e)
        .where.eq(e.workflowId, callUniquekey.workflowId)
        .and.eq(e.callFqn, callUniquekey.callFqn)
        .and.eq(e.attempt, callUniquekey.attempt)
    }.map(Job(e.resultName)).single().apply()
  }


}

final case class CallFqn(asString: String) extends AnyVal
final case class CallUniquekey(workflowId: WorkflowId,
                               callFqn: CallFqn,
                               attempt: Short,
                               jobIndexId: Int)
final case class Job(workflowId: WorkflowId,
                     callFqn: CallFqn,
                     attempt: Short,
                     jobIndex: Int,
                     vendorJobId: Option[String],
                     startTime: Instant,
                     endTime: Instant,
                     cost: Double){
  val uniqueKey = CallUniquekey(workflowId, callFqn, attempt, jobIndex)
}

object Job extends SQLSyntaxSupport[Job] {
  override def tableName: String = "JOB"
  import JobBinders._
  def apply(e: ResultName[Job])(rs: WrappedResultSet): Job = Job(
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

object JobBinders {
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