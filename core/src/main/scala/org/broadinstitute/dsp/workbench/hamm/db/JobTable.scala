package org.broadinstitute.dsp.workbench.hamm.db

import java.sql.ResultSet
import java.time.Instant

import org.broadinstitute.dsp.workbench.hamm.model._
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import scalikejdbc._


trait JobTableQueries {

  def insertJobQuery(job: Job)(implicit session: DBSession): Int
  def getJobQuery(jobUniquekey: JobUniqueKey)(implicit session: DBSession): Option[Job]
  def getJobCostQuery(jobId: CallName)(implicit session: DBSession): Option[Double]
  def getJobWorkflowCollectionIdQuery(jobId: CallName)(implicit session: DBSession): Option[WorkflowCollectionId]

}


class JobTable extends JobTableQueries with HammLogger {

  val j = Job.syntax("j")
  val w = Workflow.syntax("w")

  def insertJobQuery(job: Job)(implicit session: DBSession) = {
    import JobBinders._
    val column = Job.column
    withSQL {
      insert.into(Job).namedValues(
        column.workflowId -> job.workflowId,
        column.callName -> job.callName,
        column.attempt -> job.attempt,
        column.jobIndex -> job.jobIndex,
        column.vendorJobId -> job.vendorJobId,
        column.startTime -> job.startTime,
        column.endTime -> job.endTime,
        column.cost -> job.cost)
    }.update.apply()
  }

  def getJobQuery(jobUniquekey: JobUniqueKey)(implicit session: DBSession): Option[Job] = {
    import JobBinders._
    withSQL {
      select.from(Job as j)
        .where.eq(j.workflowId, jobUniquekey.workflowId)
        .and.eq(j.callName, jobUniquekey.callName)
        .and.eq(j.attempt, jobUniquekey.attempt)
    }.map(Job(j.resultName)).single().apply()
  }

  def getJobCostQuery(jobId: CallName)(implicit session: DBSession): Option[Double] = {
    withSQL {
      select(j.result.cost).from(Job as j)
        .where.eq(j.callName, jobId.asString) //idk if callName is the right one,
    }.map(rs => rs.double(j.resultName.cost)).single().apply()
  }

  def getJobWorkflowCollectionIdQuery(jobId: CallName)(implicit session: DBSession): Option[WorkflowCollectionId] = {
    val j = Job.syntax("j")
    val w = Workflow.syntax("w")
    withSQL {
      select(w.result.workflowCollectionId)
        .from(Workflow as w)
        .innerJoin(Job as j)
        .on(j.workflowId, w.workflowId)
        .where.eq(j.callName, jobId.asString)   //idk if callName is the right one
    }.map(rs => WorkflowCollectionId(rs.string(w.resultName.workflowCollectionId))).single().apply()
  }
}

final case class CallName(asString: String) extends AnyVal
final case class JobUniqueKey(workflowId: WorkflowId,
                              callName: CallName,
                              attempt: Short,
                              jobIndex: Int)
final case class Job(workflowId: WorkflowId,
                     callName: CallName,
                     attempt: Short,
                     jobIndex: Int,
                     vendorJobId: Option[String],
                     startTime: Instant,
                     endTime: Instant,
                     cost: Double){
  val uniqueKey = JobUniqueKey(workflowId, callName, attempt, jobIndex)
}

object Job extends SQLSyntaxSupport[Job] {
  override def tableName: String = "JOB"
  import JobBinders._
  def apply(e: ResultName[Job])(rs: WrappedResultSet): Job = Job(
    rs.get(e.workflowId),
    rs.get(e.callName),
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

  implicit val callNameTypeBinder: TypeBinder[CallName] = new TypeBinder[CallName] {
    def apply(rs: ResultSet, label: String): CallName = CallName(rs.getString(label))
    def apply(rs: ResultSet, index: Int): CallName = CallName(rs.getString(index))
  }

  implicit val workflowIdPbf: ParameterBinderFactory[WorkflowId] = ParameterBinderFactory[WorkflowId] {
    value => (stmt, idx) => stmt.setString(idx, value.id)
  }

  implicit val callNamePbf: ParameterBinderFactory[CallName] = ParameterBinderFactory[CallName] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }
}