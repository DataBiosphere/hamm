package org.broadinstitute.workbench.hamm.pricing

final case class CpuCost(asDouble: Double) extends AnyVal
final case class RamCost(asDouble: Double) extends AnyVal

final case class ComputeCost(cpuCost: CpuCost, ramCost: RamCost) {
  val totalCost = cpuCost.asDouble + ramCost.asDouble //TODO: update this
}
