package org.broadinstitute.workbench.hamm

import java.util.UUID

final case class Cpu(asString: String) extends AnyVal
final case class CpuNumber(asInt: Int) extends AnyVal
final case class BootDiskSizeGb(asInt: Int) extends AnyVal
final case class Ram(asString: String) extends AnyVal
final case class WorkflowId(uuid: UUID) extends AnyVal
final case class WorkflowCollectionId(uuid: UUID) extends AnyVal
final case class SubmissionId(uuid: UUID) extends AnyVal
final case class WorkspaceId(uuid: UUID) extends AnyVal
final case class DiskName(asString: String) extends AnyVal
final case class DiskSize(asInt: Int) extends AnyVal
final case class DiskType(asString: String) extends AnyVal
final case class Preemptible(asInt: Int) extends AnyVal
final case class MetadataResponse(value: List[Call]) extends AnyVal

final case class Call(runtimeAttributes: RuntimeAttributes, isCallCaching: Boolean, preemptible: Boolean)
final case class RuntimeAttributes(cpuNumber: CpuNumber, disks: Disks, bootDiskSizeGb: BootDiskSizeGb, preemptible: Preemptible)
final case class Disks(diskName: DiskName, diskSize: DiskSize, diskType: DiskType)
final case class Compute(cpu: Cpu, ram: Ram)
