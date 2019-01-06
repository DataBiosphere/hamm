package org.broadinstitute.workbench.ccm
package server

import org.broadinstitute.workbench.ccm.protos.ccm.WorkflowCostRequest
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genWorkflowCostRequest = Gen.alphaStr.map(x => WorkflowCostRequest(x))

  implicit val arbWorkflowCostRequest = Arbitrary(genWorkflowCostRequest)
}
