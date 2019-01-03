package org.broadinstitute.workbench.ccm

final case class Cpu(asString: String) extends AnyVal
final case class Ram(asString: String) extends AnyVal

final case class Compute(cpu: Cpu, ram: Ram)