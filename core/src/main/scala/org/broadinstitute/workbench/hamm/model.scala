package org.broadinstitute.workbench.hamm

import java.time.Instant


import java.util.UUID

final case class Cpu(asString: String) extends AnyVal
sealed trait MachineType {
  def asString: String
  def asDescriptionString: String
  def asCPUresourceGroupString: String
  def asRAMresourceGroupString: String
  def asMetadataString: String
}

object MachineType {
  private final val CUSTOM = "custom"
  private final val N1STANDARD = "N1Standard"
  private final val F1MICRO = "F1Micro"
  private final val G1SMALL = "G1Small"

  private final val CUSTOM_DESCRIPTION_STRING = "Custom Instance"
  private final val N1STANDARD_DESCRIPTION_STRING = "N1 Standard Instance"
  private final val F1MICRO_DESCRIPTION_STRING = "Micro instance"
  private final val G1SMALL_DESCRIPTION_STRING = "Small Instance"

  private final val CUSTOM_CPU_RESOURCEGROUP_STRING = "CPU"
  private final val CUSTOM_RAM_RESOURCEGROUP_STRING = "RAM"

  private final val CUSTOM_METADATA_STRING = "custom"
  private final val N1STANDARD_METADATA_STRING = "n1-standard"
  private final val F1MICRO_METADATA_STRING = "f1-micro"
  private final val G1SMALL_METADATA_STRING = "g1-small"

  val stringToMachineType = Map(
      CUSTOM_METADATA_STRING -> Custom,
      N1STANDARD_METADATA_STRING -> N1Standard,
      F1MICRO_METADATA_STRING -> F1Micro,
      G1SMALL_METADATA_STRING -> G1Small
  )


  case object Custom extends MachineType {
    def asString = CUSTOM
    def asDescriptionString: String = CUSTOM_DESCRIPTION_STRING
    def asCPUresourceGroupString: String = CUSTOM_CPU_RESOURCEGROUP_STRING
    def asRAMresourceGroupString: String = CUSTOM_RAM_RESOURCEGROUP_STRING
    def asMetadataString: String = CUSTOM_METADATA_STRING
  }

  case object N1Standard extends MachineType {
    def asString = N1STANDARD
    def asDescriptionString: String = N1STANDARD_DESCRIPTION_STRING
    def asCPUresourceGroupString: String = N1STANDARD
    def asRAMresourceGroupString: String = N1STANDARD
    def asMetadataString: String = N1STANDARD_METADATA_STRING
  }

  case object F1Micro extends MachineType {
    def asString = F1MICRO
    def asDescriptionString: String = F1MICRO_DESCRIPTION_STRING
    def asCPUresourceGroupString: String = F1MICRO
    def asRAMresourceGroupString: String = F1MICRO
    def asMetadataString: String = F1MICRO_METADATA_STRING
  }

  case object G1Small extends MachineType {
    def asString = G1SMALL
    def asDescriptionString: String = G1SMALL_DESCRIPTION_STRING
    def asCPUresourceGroupString: String = G1SMALL
    def asRAMresourceGroupString: String = G1SMALL
    def asMetadataString: String = G1SMALL_METADATA_STRING
  }
}

sealed trait Region {
  def asString: String
}
object Region {
  private final val GLOBAL = "global"
  private final val US = "us"
  private final val USCENTRAL1 = "us-central1"
  private final val USEAST1 = "us-east1"
  private final val USWEST4 = "us-east4"
  private final val USWEST1 = "us-west1"
  private final val USWEST2 = "us-west2"
  private final val EUROPE =  "europe"
  private final val EUROPEWEST1 = "europe-west1"
  private final val EUROPEWEST2 = "europe-west2"
  private final val EUROPEWEST3 = "europe-west3"
  private final val EUROPEWEST4 = "europe-west4"
  private final val EUROPENORTH1 = "europe-north1"
  private final val NORTHAMERICANORTHEAST1 = "northamerica-northeast1"
  private final val ASIA = "asia"
  private final val ASIAEAST = "asia-east"
  private final val ASIAEAST1 = "asia-east1"
  private final val ASIAEAST2 = "asia-east2"
  private final val ASIANORTHEAST = "asia-northeast"
  private final val ASIASOUTHEAST = "asia-southeast"
  private final val AUSTRALIASOUTHEAST1 = "australia-southeast1"
  private final val AUSTRALIA = "australia"
  private final val SOUTHAMERICAEAST1 = "southamerica-east1"
  private final val ASIASOUTH1 = "asia-south1"

  //ToDo: string might be in the format "us-central1-c" from Cromwell, handle those cases too
  val stringToRegion = Map(
      GLOBAL -> Global,
      US -> Us,
      USCENTRAL1 -> Uscentral1,
      USEAST1 -> Useast1,
      USWEST4 -> Uswest4,
      USWEST1 -> Uswest1,
      USWEST2 -> Uswest2,
      EUROPE -> Europe,
      EUROPEWEST1 -> Europewest1,
      EUROPEWEST2 -> Europewest2,
      EUROPEWEST3 -> Europewest3,
      EUROPEWEST4 -> Europewest4,
      EUROPENORTH1 -> Europenorth1,
      NORTHAMERICANORTHEAST1 -> Northamericanortheast1,
      ASIA -> Asia,
      ASIAEAST -> Asiaeast,
      ASIAEAST1 -> Asiaeast1,
      ASIAEAST2 -> Asiaeast2,
      ASIANORTHEAST -> Asianortheast,
      ASIASOUTHEAST -> Asiasoutheast,
      AUSTRALIASOUTHEAST1 -> Australiasoutheast1,
      AUSTRALIA -> Australia,
      SOUTHAMERICAEAST1 -> Southamericaeast1,
      ASIASOUTH1 -> Asiasouth1 )

  case object Global extends Region {
    def asString = GLOBAL
  }
  case object Us extends Region {
    def asString = US
  }
  case object Uscentral1 extends Region {
    def asString = USCENTRAL1
  }
  case object Useast1 extends Region {
    def asString = USEAST1
  }
  case object Uswest4 extends Region {
    def asString = USWEST4
  }
  case object Uswest1 extends Region {
    def asString = USWEST1
  }
  case object Uswest2 extends Region {
    def asString = USWEST2
  }
  case object Europe extends Region {
    def asString = EUROPE
  }
  case object Europewest1 extends Region {
    def asString = EUROPEWEST1
  }
  case object Europewest2 extends Region {
    def asString = EUROPEWEST2
  }
  case object Europewest3 extends Region {
    def asString = EUROPEWEST3
  }
  case object Europewest4 extends Region {
    def asString = EUROPEWEST4
  }
  case object Europenorth1 extends Region {
    def asString = EUROPENORTH1
  }
  case object Northamericanortheast1 extends Region {
    def asString = NORTHAMERICANORTHEAST1
  }
  case object Asia extends Region {
    def asString = ASIA
  }
  case object Asiaeast extends Region {
    def asString = ASIAEAST
  }
  case object Asiaeast1 extends Region {
    def asString = ASIAEAST1
  }
  case object Asiaeast2 extends Region {
    def asString = ASIAEAST2
  }
  case object Asianortheast extends Region {
    def asString = ASIANORTHEAST
  }
  case object Asiasoutheast extends Region {
    def asString = ASIASOUTHEAST
  }
  case object Australiasoutheast1 extends Region {
    def asString = AUSTRALIASOUTHEAST1
  }
  case object Australia extends Region {
    def asString = AUSTRALIA
  }
  case object Southamericaeast1 extends Region {
    def asString = SOUTHAMERICAEAST1
  }
  case object Asiasouth1 extends Region {
    def asString = ASIASOUTH1
  }
}

sealed trait DiskType {
  def asString: String
  def asDescriptionString: String
  def asResourceGroupString: String
}

object DiskType {
  private final val SSD_STRING = "SSD"
  private final val HDD_STRING = "HDD"

  private final val SSD_DESCRIPTION_STRING = "SSD"
  private final val HDD_DESCRIPTION_STRING = "PD"

  private final val SSD_RESOURCE_GROUP_STRING = "SSD"
  private final val HDD_RESOURCE_GROUP_STRING = "PDStandard"

  val stringToDiskType = Map (
    SSD_STRING -> SSD,
    HDD_STRING -> HDD
  )

  case object SSD extends DiskType {
    def asString = SSD_STRING
    def asDescriptionString = SSD_DESCRIPTION_STRING
    def asResourceGroupString = SSD_RESOURCE_GROUP_STRING
  }

  case object HDD extends DiskType {
    def asString = HDD_STRING
    def asDescriptionString = HDD_DESCRIPTION_STRING
    def asResourceGroupString = HDD_RESOURCE_GROUP_STRING
  }
}

sealed trait Status {
  def asString: String
}

object Status {
  private final val RUNNING = "Running"
  private final val NOTSTARTED = "NotStarted"
  private final val STARTING = "Starting"
  private final val FAILED = "Failed"
  private final val DONE = "Done"

  final val inFlightStatuses = List(Running, NotStarted, Starting)
  final val terminalStatuses = List(Failed, Done)

  val stringToStatus = Map(
    RUNNING -> Running,
    NOTSTARTED -> NotStarted,
    STARTING -> Starting,
    FAILED -> Failed,
    DONE -> Done
  )

  case object Done extends Status {
    def asString = DONE
  }

  case object Running extends Status {
    def asString = RUNNING
  }

  case object NotStarted extends Status {
     def asString = NOTSTARTED
  }

  case object Starting extends Status {
    def asString = STARTING
  }

  case object Failed extends Status {
    def asString = FAILED
  }
  //see if there are others?

}

sealed trait BackEnd {
  def asString: String
}

object BackEnd {
  private final val JES = "JES"
  private final val PAPIV2 = "PAPIV2" //???

  val stringToBackEnd = Map(
    JES -> Jes,
    PAPIV2 -> PapiV2
  )

  case object Jes extends BackEnd {
    def asString = JES
  }

  case object PapiV2 extends BackEnd {
    def asString = PAPIV2
  }

}

final case class CpuNumber(asInt: Int) extends AnyVal
final case class BootDiskSizeGb(asInt: Int) extends AnyVal
final case class WorkflowId(uuid: UUID) extends AnyVal
final case class WorkflowCollectionId(uuid: UUID) extends AnyVal
final case class SubmissionId(uuid: UUID) extends AnyVal
final case class WorkspaceId(uuid: UUID) extends AnyVal
final case class DiskName(asString: String) extends AnyVal
final case class DiskSize(asInt: Int) extends AnyVal
final case class PreemptibleAttemptsAllowed(asInt: Int) extends AnyVal
final case class Attempt(asInt: Int) extends AnyVal
final case class ExecutionEventDescription(asString: String) extends AnyVal


final case class ExecutionEvent(description: ExecutionEventDescription,
                                startTime: Instant,
                                endTime: Instant)

final case class Call(runtimeAttributes: RuntimeAttributes,
                      executionEvents: List[ExecutionEvent],
                      isCallCaching: Boolean,
                      preemptible: Boolean,
                      region: Region,
                      status: Status,
                      machineType: MachineType,
                      backend: BackEnd,
                      attempt: Attempt)

final case class MetadataResponse(calls: List[Call], startTime: Instant, endTime: Instant)


final case class RuntimeAttributes(cpuNumber: CpuNumber,
                                   disks: Disks,
                                   bootDiskSizeGb: BootDiskSizeGb,
                                   preemptibleAttemptsAllowed: PreemptibleAttemptsAllowed)

final case class Disks(diskName: DiskName, diskSize: DiskSize, diskType: DiskType)
