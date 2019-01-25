package org.broadinstitute.workbench.hamm

import java.time.Instant

import org.broadinstitute.workbench.hamm.db.{CallFqn, JobCost, Label, WorkflowDB}
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genCpu = Gen.alphaStr.map(Cpu)
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)
  val genWorkflowId = Gen.uuid.map(WorkflowId)
  val genSubmissionId = Gen.uuid.map(SubmissionId)
  val genWorkspaceId = Gen.uuid.map(WorkspaceId)
  val genLabelMap: Gen[Map[String, String]] = Gen.mapOf[String, String](Gen.listOfN(2, Gen.alphaStr).map(x => (x(0), x(1))))
  val genNonEmptyLabelMap: Gen[Map[String, String]] = Gen.nonEmptyMap[String, String](Gen.listOfN(2, Gen.alphaStr.map(x => s"ll$x")).map(x => (x(0), x(1))))
  val genLabel: Gen[Label] = for {
    name <- Gen.nonEmptyListOf[Char](Gen.alphaChar)
    value <- Gen.nonEmptyListOf[Char](Gen.alphaChar)
  } yield Label(name.mkString(""), value.mkString(""))

  val genWorkflowDb = for {
    id <- genWorkflowId
    parentWorkflowId <- Gen.option(genWorkflowId)
    rootWorkflowId <- Gen.option(genWorkflowId)
    isSubWorkflow <- Gen.oneOf(true, false)
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    label <- genLabelMap
    cost <- Gen.posNum[Double]
  } yield WorkflowDB(id, parentWorkflowId, rootWorkflowId, isSubWorkflow, startTime, endTime, label, cost)

  val genListOfWorkflowDBWithSameLabel = for {
    label <- genLabel
    workflows <- Gen.nonEmptyListOf(genWorkflowDb)
  } yield workflows.map(x => x.copy(label = Map(label.key -> label.value)))

  val genJobCost = for{
    workflowId <- genWorkflowId
    callFqn <- Gen.alphaStr.map(x => CallFqn(s"callFqn$x"))
    attempt <- Gen.posNum[Short]
    jobIndexId <- Gen.posNum[Int]
    vendorJobId <- Gen.option(Gen.alphaStr.map(s => s"operationId$s"))
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    cost <- Gen.posNum[Double]
  } yield JobCost(workflowId, callFqn, attempt, jobIndexId, vendorJobId, startTime, endTime, cost)

  val genNonEmptyLabelWorkflowDb = for {
    label <- genNonEmptyLabelMap
    workflowDb <- genWorkflowDb
  } yield workflowDb.copy(label = label)

  implicit val arbJobCostDb = Arbitrary(genJobCost)
}
