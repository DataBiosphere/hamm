package org.broadinstitute.workbench.hamm
package db

import java.sql.ResultSet
import java.time.Instant

import scalikejdbc._
import io.circe._
import io.circe.parser._
import io.circe.syntax._

import org.broadinstitute.workbench.hamm.model._


object WorkflowTableQueries {

  def insertWorkflowCostSql(workflow: Workflow)(implicit session: DBSession) = {
    import WorkflowBinders._
    val column = Workflow.column
    withSQL {
      insert.into(Workflow).namedValues(
        column.workflowId -> workflow.workflowId,
        column.parentWorkflowId -> workflow.parentWorkflowId,
        column.rootWorkflowId -> workflow.rootWorkflowId,
        column.workflowCollectionId -> workflow.workflowCollectionId,
        column.isSubWorkflow -> workflow.isSubWorkflow,
        column.startTime -> workflow.startTime,
        column.endTime -> workflow.endTime,
        column.labels -> workflow.labels,
        column.cost -> workflow.cost)
    }.update.apply()
  }

  def getWorkflowSql(workflowId: WorkflowId)(implicit session: DBSession): Option[Workflow] = {
  //  import WorkflowBinders._
    val e = Workflow.syntax("e")
    withSQL {
      select.from(Workflow as e)
        .where.eq(e.workflowId, workflowId.id)
    }.map(Workflow(e.resultName)).single().apply()
  }
}

final case class Workflow(
    workflowId: WorkflowId,
    parentWorkflowId: Option[WorkflowId],
    rootWorkflowId: Option[WorkflowId],
    workflowCollectionId: WorkflowCollectionId,
    isSubWorkflow: Boolean,
    startTime: Instant,
    endTime: Instant,
    labels: Map[String, String],
    cost: Double)

final case class Label(key: String, value: String)
//final case class WorkflowCost(workflowId: WorkflowId, cost: Double)

object Workflow extends SQLSyntaxSupport[Workflow] {
  override def tableName: String = "WORKFLOW"
  import WorkflowBinders._
  def apply(e: ResultName[Workflow])(rs: WrappedResultSet): Workflow = Workflow(
    rs.get(e.workflowId),
    rs.get(e.parentWorkflowId),
    rs.get(e.rootWorkflowId),
    rs.get(e.workflowCollectionId),
    rs.get(e.isSubWorkflow),
    rs.get(e.startTime),
    rs.get(e.endTime),
    rs.get(e.labels),
    rs.get(e.cost)
  )
}

// ToDo: Fix this
object WorkflowBinders {
  implicit val workflowIdTypeBinder: TypeBinder[WorkflowId] = new TypeBinder[WorkflowId] {
    def apply(rs: ResultSet, label: String): WorkflowId = WorkflowId(rs.getString(label))
    def apply(rs: ResultSet, index: Int): WorkflowId = WorkflowId(rs.getString(index))
  }

  implicit val WorkflowCollectionIdTypeBinder: TypeBinder[WorkflowCollectionId] = new TypeBinder[WorkflowCollectionId] {
    def apply(rs: ResultSet, label: String): WorkflowCollectionId = WorkflowCollectionId(rs.getString(label))
    def apply(rs: ResultSet, index: Int): WorkflowCollectionId = WorkflowCollectionId(rs.getString(index))
  }


  //ToDo: Make this better
  implicit val LabelTypeBinder: TypeBinder[Map[String,String]] = {
    TypeBinder.option[String].map { strOption =>
      strOption.flatMap(str => parse(str) match {
        case Left(parsingFailure: ParsingFailure) => throw parsingFailure
        case Right(json: Json) => json.as[Map[String, String]] match {
          case Left(decodingFailure: DecodingFailure) => throw decodingFailure
          case Right(labels: Map[String, String]) => Some(labels)
        }
      }).get
      }
  }

  implicit val workflowIdPbf: ParameterBinderFactory[WorkflowId] = ParameterBinderFactory[WorkflowId] {
    value => (stmt, idx) => stmt.setString(idx, value.id)
  }

  implicit val WorkflowCollectionIdPbf: ParameterBinderFactory[WorkflowCollectionId] = ParameterBinderFactory[WorkflowCollectionId] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val LabelPbf: ParameterBinderFactory[Map[String, String]] = ParameterBinderFactory[Map[String, String]] {
    value => (stmt, idx) => stmt.setString(idx, value.asJson.noSpaces)
  }


}