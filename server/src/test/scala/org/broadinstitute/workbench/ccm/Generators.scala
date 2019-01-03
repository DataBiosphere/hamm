package org.broadinstitute.workbench.ccm

import org.broadinstitute.workbench.ccm.protos.workflow.WorkflowCostRequest
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genWorkflowCostRequest = Gen.posNum[Long].map(x => WorkflowCostRequest(x))
  implicit val arbWorkflowCostRequest = Arbitrary(genWorkflowCostRequest)
}
