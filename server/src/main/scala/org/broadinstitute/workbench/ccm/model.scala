package org.broadinstitute.workbench.ccm

import cats.Eq
import cats.implicits._
import io.circe.Decoder
import org.broadinstitute.workbench.ccm.protos.workflow.WorkflowCostResponse

final case class Cpu(asString: String) extends AnyVal
final case class CpuNumber(asInt: Int) extends AnyVal
final case class BootDiskSizeGb(asInt: Int) extends AnyVal
final case class Ram(asString: String) extends AnyVal
final case class WorkflowId(id: String) extends AnyVal
final case class DiskName(asString: String) extends AnyVal
final case class DiskSize(asInt: Int) extends AnyVal
final case class DiskType(asString: String) extends AnyVal
final case class Preemptible(asInt: Int) extends AnyVal
final case class MetadataResponse(value: List[Call]) extends AnyVal

final case class Call(runtimeAttributes: RuntimeAttributes, isCallCaching: Boolean, preemptible: Boolean)
final case class RuntimeAttributes(cpuNumber: CpuNumber, disks: Disks, bootDiskSizeGb: BootDiskSizeGb, preemptible: Preemptible)
final case class Disks(diskName: DiskName, diskSize: DiskSize, diskType: DiskType)
final case class Compute(cpu: Cpu, ram: Ram)

object model {
  implicit val cpuNumberDecoder: Decoder[CpuNumber] = Decoder.decodeString.emap(s => Either.catchNonFatal(s.toInt).leftMap(_.getMessage).map(CpuNumber))
  implicit val bootDiskSizeGbDecoder: Decoder[BootDiskSizeGb] = Decoder.decodeString.emap(x => Either.catchNonFatal(x.toInt).leftMap(_.getMessage).map(BootDiskSizeGb))
  implicit val preemptibleDecoder: Decoder[Preemptible] = Decoder.decodeString.emap{
    s =>
      Either.catchNonFatal(s.toInt).leftMap(_.getMessage).map(Preemptible)
  }
  implicit val diskNameDecoder: Decoder[Disks] = Decoder.decodeString.emap{
    str =>
      // sample value for str: `local-disk 1 HDD`
      for{
        array <- Either.catchNonFatal(str.split(" ")).leftMap(_.getMessage)
        size <- Either.catchNonFatal(array(1).toInt).leftMap(_.getMessage)
      } yield Disks(DiskName(array(0)), DiskSize(size), DiskType(array(2)))
  }
  implicit val runtimeAttributesDecoder: Decoder[RuntimeAttributes] = Decoder.forProduct4("cpu", "disks", "bootDiskSizeGb", "preemptible")(RuntimeAttributes)
  implicit val callDecoder: Decoder[Call] = Decoder.instance{
    cursor =>
      for {
        ra <- cursor.downField("runtimeAttributes").as[RuntimeAttributes]
        isPreemptible <- cursor.downField("preemptible").as[Boolean]
        isCallCaching <- cursor.downField("callCaching").downField("hit").as[Boolean]
      } yield Call(ra, isCallCaching, isPreemptible)
  }
  implicit val metadataResponseDecoder: Decoder[MetadataResponse] = Decoder.instance {
    cursor =>
      cursor.downField("calls").as[Map[String, List[Call]]].map(x => MetadataResponse(x.values.flatten.toList))
  }
  implicit val eqWorkflowCostRequest: Eq[WorkflowCostResponse] = Eq.instance((x, y) => x.cost == y.cost)
}