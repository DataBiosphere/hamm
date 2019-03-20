package org.broadinstitute.dsp.workbench.hamm

import java.time.Instant

import org.broadinstitute.dsp.workbench.hamm.db._
import org.broadinstitute.dsp.workbench.hamm.model._
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)
  val genWorkflowId = Gen.alphaLowerStr.map(WorkflowId)
  val genSubmissionId = Gen.uuid.map(SubmissionId)
  val genWorkspaceId = Gen.uuid.map(WorkspaceId)
  val genWorkflowCollectionId = Gen.alphaLowerStr.map(WorkflowCollectionId)
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
    workflowCollectionId <- genWorkflowCollectionId
    isSubWorkflow <- Gen.oneOf(true, false)
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    label <- genLabelMap
    cost <- Gen.posNum[Double]
  } yield Workflow(id, parentWorkflowId, rootWorkflowId, workflowCollectionId, isSubWorkflow, startTime, endTime, label, cost)

  val genListOfWorkflowDBWithSameLabel = for {
    labels <- genLabel
    workflows <- Gen.nonEmptyListOf(genWorkflowDb)
  } yield workflows.map(x => x.copy(labels = Map(labels.key -> labels.value)))

  val genJobCost = for{
    workflowId <- genWorkflowId
    callFqn <- Gen.alphaStr.map(x => CallFqn(s"callFqn$x"))
    attempt <- Gen.posNum[Short]
    jobIndexId <- Gen.posNum[Int]
    vendorJobId <- Gen.option(Gen.alphaStr.map(s => s"operationId$s"))
    startTime <- Gen.const(Instant.now())
    endTime <- Gen.const(Instant.now())
    cost <- Gen.posNum[Double]
  } yield Job(workflowId, callFqn, attempt, jobIndexId, vendorJobId, startTime, endTime, cost)

  val genNonEmptyLabelWorkflowDb = for {
    labels <- genNonEmptyLabelMap
    workflowDb <- genWorkflowDb
  } yield workflowDb.copy(labels = labels)

  implicit val arbJobCostDb = Arbitrary(genJobCost)
}
