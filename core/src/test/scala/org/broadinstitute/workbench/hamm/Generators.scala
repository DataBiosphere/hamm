package org.broadinstitute.workbench.hamm

import java.time.Instant

import org.broadinstitute.workbench.ccm.db.Label.{Submission, Workspace}
import org.broadinstitute.workbench.ccm.db.{Label, WorkflowDB}
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genCpu = Gen.alphaStr.map(Cpu)
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)
  val genWorkflowId = Gen.uuid.map(WorkflowId)
  val genLabel: Gen[Label] = for {
    value <- Gen.alphaStr
    label <- Gen.oneOf(Submission(value), Workspace(value))
  } yield label

  val genWorkflowDb = for {
    id <- genWorkflowId
    endTime <- Gen.const(Instant.now())
    label <- Gen.option[Label](genLabel)
    cost <- Gen.posNum[Double]
  } yield WorkflowDB(id, endTime, label, cost)

  implicit val arbWorkflowDb = Arbitrary(genWorkflowDb)
}
