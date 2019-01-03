package org.broadinstitute.workbench.ccm

import cats.Eq
import org.broadinstitute.workbench.ccm.protos.workflow.WorkflowCostResponse

final case class Cpu(asString: String) extends AnyVal
final case class Ram(asString: String) extends AnyVal

final case class Compute(cpu: Cpu, ram: Ram)

object model {
  implicit val eqWorkflowCostRequest: Eq[WorkflowCostResponse] = Eq.instance((x, y) => x.cost == y.cost)
}