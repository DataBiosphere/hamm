package org.broadinstitute.workbench.ccm

import java.time.Instant
import java.util.UUID
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import cats._
import cats.data._
import cats.implicits._

package object db {
  implicit val instantPut: Put[Instant] = Meta[Instant].put
  implicit val instantGet: Get[Instant] = Meta[Instant].get
  implicit val workflowIdMeta: Meta[WorkflowId] = Meta[UUID].timap[WorkflowId](uuid => WorkflowId(uuid))(_.uuid)
  implicit val workflowIdPut: Put[WorkflowId] = workflowIdMeta.put
  implicit val workflowIdGet: Get[WorkflowId] = workflowIdMeta.get
  implicit val submissionMeta: Meta[SubmissionId] = Meta[UUID].timap[SubmissionId](uuid => SubmissionId(uuid))(_.uuid)
  implicit val submissionPut: Put[SubmissionId] = submissionMeta.put
  implicit val submissionGet: Get[SubmissionId] = submissionMeta.get
  implicit val workspaceIdMeta: Meta[WorkspaceId] = Meta[UUID].timap[WorkspaceId](uuid => WorkspaceId(uuid))(_.uuid)
  implicit val workspaceIdPut: Put[WorkspaceId] = workspaceIdMeta.put
  implicit val workspaceIdGet: Get[WorkspaceId] = workspaceIdMeta.get
  implicit val callFqnGet: Get[CallFqn] = Get[String].map(CallFqn)
  implicit val listWorkflowPut: Put[Option[NonEmptyList[WorkflowId]]] = Put[List[UUID]].contramap(x => x.fold(List.empty[UUID])(nl => nl.map(_.uuid).toList))

  implicit val labelRead: Read[Option[Label]] = Read[(Option[String], Option[String])].map[Option[Label]] { x =>
    x match {
      case (Some("submission"), Some(v)) => Some(Label.Submission(v))
      case (Some("workspace"), Some(v)) => Some(Label.Workspace(v))
      case _ => None
    }
  }
  implicit val labelWrite: Write[Option[Label]] = Write[(Option[(String, String)])].contramap[Option[Label]] { x =>
    x.map(label => (label.name, label.labelValue))
  }
//  implicit val workflowDBRead: Read[WorkflowDB] =
//    Read[(WorkflowId, List[WorkflowId], Boolean, SubmissionId, WorkspaceId, String, Instant, Instant, Option[Label], Double)].map(x => WorkflowDB(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10))
  implicit val workflowDBWrite: Write[WorkflowDB] =
    Write[(WorkflowId, Option[NonEmptyList[WorkflowId]], Boolean, SubmissionId, WorkspaceId, UUID, Instant, Instant, Option[Label], Double)].contramap[WorkflowDB](x => WorkflowDB.unapply(x).get)

  implicit val workflowCost: Read[WorkflowCost] = Read[(WorkflowId, Double)].map(x => WorkflowCost(x._1, x._2))

  val workflowTableName = Fragment.const("WORKFLOW_COST")
  val jobTableName = Fragment.const("JOB_COST")

  val workflowIdFieldName = "WORKFLOW_ID"
  val subWorkflowIdFieldName = "SUB_WORKFLOW_ID"
  val isSubWorkflowFieldName = "IS_SUB_WORKFLOW"
  val submissionIdFieldName = "SUBMISSION_ID"
  val workspaceIdFieldName = "WORKSPACE_ID"
  val billingProjectIdFieldName = "BILLING_PROJECT_ID"
  val startTimeFieldName = "START_TIME"
  val endTimeFieldName = "END_TIME"
  val labelNameFieldName = "LABEL_NAME"
  val labelValueFieldName = "LABEL_VALUE"
  val costFieldName = "COST"
  val callFqnFieldName = "CALL_FQN"
  val attemptFieldName = "ATTEMPT"
  val gcpJobIdFieldName = "GCP_JOB_ID"

  val workflowIdFragment = Fragment.const(workflowIdFieldName)
  val subWorkflowIdFragment = Fragment.const(subWorkflowIdFieldName)
  val isSubWorkflowFragment = Fragment.const(isSubWorkflowFieldName)
  val submissionIdFragment = Fragment.const(submissionIdFieldName)
  val workspaceIdFragment = Fragment.const(workspaceIdFieldName)
  val billingProjectIdFragment = Fragment.const(billingProjectIdFieldName)
  val startTimeFragment = Fragment.const(startTimeFieldName)
  val endTimeFragment = Fragment.const(endTimeFieldName)
  val labelNameFragment = Fragment.const(labelNameFieldName)
  val labelValueFragment = Fragment.const(labelValueFieldName)
  val costFragment = Fragment.const(costFieldName)
  val callFqnFragment = Fragment.const(callFqnFieldName)
  val attemptFragment = Fragment.const(attemptFieldName)
  val gcpJobIdFragment = Fragment.const(gcpJobIdFieldName)
}
