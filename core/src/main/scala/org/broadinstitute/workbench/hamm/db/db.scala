package org.broadinstitute.workbench.hamm

import java.time.Instant
import java.util.UUID

import doobie._
import doobie.postgres.implicits._
import cats.data._
import cats.implicits._
import io.circe._
import io.circe.syntax._
import org.postgresql.util.PGobject

package object db {
  implicit val instantPut: Put[Instant] = Meta[Instant].put
  implicit val instantGet: Get[Instant] = Meta[Instant].get
  implicit val workflowIdMeta: Meta[WorkflowId] = Meta[UUID].timap[WorkflowId](uuid => WorkflowId(uuid))(_.uuid)
  implicit val workflowIdPut: Put[WorkflowId] = workflowIdMeta.put
  implicit val workflowIdGet: Get[WorkflowId] = workflowIdMeta.get
  implicit val workflowCollectionIdMeta: Meta[WorkflowCollectionId] = Meta[UUID].timap[WorkflowCollectionId](uuid => WorkflowCollectionId(uuid))(_.uuid)
  implicit val workflowCollectionIdPut: Put[WorkflowCollectionId] = workflowCollectionIdMeta.put
  implicit val workflowCollectionIdGet: Get[WorkflowCollectionId] = workflowCollectionIdMeta.get
  implicit val callFqnGet: Get[CallFqn] = Get[String].map(CallFqn)
  implicit val listWorkflowPut: Put[Option[NonEmptyList[WorkflowId]]] = Put[List[UUID]].contramap(x => x.fold(List.empty[UUID])(nl => nl.map(_.uuid).toList))
  implicit val listWorkflowGet: Get[List[WorkflowId]] = Get[List[UUID]].map(x => x.map(WorkflowId))
  val jsonbMeta: Meta[Json] = Meta
    .Advanced
    .other[PGobject]("jsonb")
    .timap[Json](
    jsonStr => parser.parse(jsonStr.getValue).leftMap[Json](err => throw err).merge){
    json =>
      val o = new PGobject
      o.setType("jsonb")
      o.setValue(json.noSpaces)
      o
    }

  implicit val labelsGet: Get[Map[String, String]] = jsonbMeta.get.map{
    json =>
      json.as[Map[String, String]].leftMap[Map[String, String]](throw _).merge
  }
  implicit val labelPut: Put[Map[String, String]] = jsonbMeta.put.contramap[Map[String, String]](x => x.asJson)
  implicit val workflowDBRead: Read[WorkflowDB] =
    Read[(WorkflowId, Option[WorkflowId], Option[WorkflowId], WorkflowCollectionId, Boolean, Instant, Instant, Option[Map[String, String]], Double)].map(x => WorkflowDB(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8.getOrElse(Map.empty), x._9))
  implicit val workflowDBWrite: Write[WorkflowDB] =
    Write[(WorkflowId, Option[WorkflowId], Option[WorkflowId], WorkflowCollectionId, Boolean, Instant, Instant, Map[String, String], Double)].contramap[WorkflowDB](x => WorkflowDB.unapply(x).get)

  implicit val workflowCost: Read[WorkflowCost] = Read[(WorkflowId, Double)].map(x => WorkflowCost(x._1, x._2))

  val workflowTableName = Fragment.const("WORKFLOW_COST")
  val jobTableName = Fragment.const("JOB_COST")

  val workflowIdFieldName = "WORKFLOW_ID"
  val parentWorkflowIdFieldName = "PARENT_WORKFLOW_ID"
  val rootWorkflowIdFieldName = "ROOT_WORKFLOW_ID"
  val workflowCollectionIdFieldName = "WORKFLOW_COLLECTION_ID"
  val isSubWorkflowFieldName = "IS_SUB_WORKFLOW"
  val startTimeFieldName = "START_TIME"
  val endTimeFieldName = "END_TIME"
  val labelsFieldName = "LABELS"
  val costFieldName = "COST"
  val callFqnFieldName = "CALL_FQN"
  val attemptFieldName = "ATTEMPT"
  val jobIndexFieldName = "JOB_INDEX"
  val vendorJobIdFieldName = "VENDOR_JOB_ID"

  val workflowIdFragment = Fragment.const(workflowIdFieldName)
  val parentWorkflowIdFragment = Fragment.const(parentWorkflowIdFieldName)
  val rootWorkflowIdFragment = Fragment.const(rootWorkflowIdFieldName)
  val workflowCollectionIdFragment = Fragment.const(workflowCollectionIdFieldName)
  val isSubWorkflowFragment = Fragment.const(isSubWorkflowFieldName)
  val startTimeFragment = Fragment.const(startTimeFieldName)
  val endTimeFragment = Fragment.const(endTimeFieldName)
  val labelsFragment = Fragment.const(labelsFieldName)
  val costFragment = Fragment.const(costFieldName)
  val callFqnFragment = Fragment.const(callFqnFieldName)
  val attemptFragment = Fragment.const(attemptFieldName)
  val jobIndexFragment = Fragment.const(jobIndexFieldName)
  val vendorJobIdFragment = Fragment.const(vendorJobIdFieldName)
}
