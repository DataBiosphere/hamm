package org.broadinstitute.workbench

import cats.Eq
import org.broadinstitute.workbench.protos.ccm.WorkflowCostResponse

package object ccm {
  implicit val eqWorkflowCostResponse: Eq[WorkflowCostResponse] = Eq.instance((x, y) => x.cost == y.cost)
}
