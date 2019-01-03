package org.broadinstitute.workbench.ccm

import org.broadinstitute.workbench.ccm.protos.workflow.WorkflowCostRequest
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genWorkflowCostRequest = Gen.alphaStr.map(x => WorkflowCostRequest(x))
  val genCpu = Gen.alphaStr.map(Cpu)
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)

  implicit val arbWorkflowCostRequest = Arbitrary(genWorkflowCostRequest)
}
