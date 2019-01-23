package org.broadinstitute.workbench.hamm

import java.time.Instant

import cats.data.NonEmptyList
import org.broadinstitute.workbench.ccm.db.{CallCost, CallFqn, Label, WorkflowDB}
import org.broadinstitute.workbench.ccm.db.Label.{Submission, Workspace}
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genCpu = Gen.alphaStr.map(Cpu)
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)
  val genWorkflowId = Gen.uuid.map(WorkflowId)
  val genSubmissionId = Gen.uuid.map(SubmissionId)
  val genWorkspaceId = Gen.uuid.map(WorkspaceId)
  val genLabel: Gen[Label] = for {
    value <- Gen.alphaStr
    label <- Gen.oneOf(Submission(value), Workspace(value))
  } yield label

  val genWorkflowDb = for {
    id <- genWorkflowId
    subIds <- Gen.listOf(genWorkflowId).map(NonEmptyList.fromList)
    submissionId <- genSubmissionId
    workspaceId <- genWorkspaceId
    billingProjectId <- Gen.uuid
    isSubWorkflow <- Gen.oneOf(true, false)
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    label <- Gen.option[Label](genLabel)
    cost <- Gen.posNum[Double]
  } yield WorkflowDB(id, subIds, isSubWorkflow, submissionId, workspaceId, billingProjectId, startTime, endTime, label, cost)

  val genCallCost = for{
    workflowId <- genWorkflowId
    callFqn <- Gen.alphaStr.map(x => CallFqn(s"callFqn$x"))
    attempt <- Gen.posNum[Short]
    gcpJobsId <- Gen.option(Gen.alphaStr.map(s => s"operationId$s"))
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    cost <- Gen.posNum[Double]
  } yield CallCost(workflowId, callFqn, attempt, gcpJobsId, startTime, endTime, cost)

  implicit val arbWorkflowDb = Arbitrary(genWorkflowDb)
}
