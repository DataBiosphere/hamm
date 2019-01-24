package org.broadinstitute.workbench.hamm
package server

import org.broadinstitute.workbench.hamm.protos.hamm.WorkflowCostRequest
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genWorkflowCostRequest = Gen.alphaStr.map(x => WorkflowCostRequest(x))

  implicit val arbWorkflowCostRequest = Arbitrary(genWorkflowCostRequest)
}
