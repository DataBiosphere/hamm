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
  final val CUSTOM = "custom"
  final val N1STANDARD = "N1Standard"
  final val F1MICRO = "F1Micro"
  final val G1SMALL = "G1Small"

  final val CUSTOM_DESCRIPTION_STRING = "Custom Instance"
  final val N1STANDARD_DESCRIPTION_STRING = "N1 Standard Instance"
  final val F1MICRO_DESCRIPTION_STRING = "Micro instance"
  final val G1SMALL_DESCRIPTION_STRING = "Small Instance"

  final val CUSTOM_CPU_RESOURCEGROUP_STRING = "CPU"
  final val CUSTOM_RAM_RESOURCEGROUP_STRING = "RAM"

  final val CUSTOM_METADATA_STRING = "custom"
  final val N1STANDARD_METADATA_STRING = "n1-standard"
  final val F1MICRO_METADATA_STRING = "f1-micro"
  final val G1SMALL_METADATA_STRING = "g1-small"

  final val allMachineTypes = Seq(Custom, N1Standard, F1Micro, G1Small)

//  val resourceGroupToMachineType = Map(
//    CUSTOM -> Custom,
//    N1STANDARD -> N1Standard,
//    F1MICRO -> F1Micro,
//    G1SMALL -> G1Small)

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
  final val GLOBAL = "global"
  final val US = "us"
  final val USCENTRAL1 = "us-central1"
  final val USEAST1 = "us-east1"
  final val USWEST4 = "us-east4"
  final val USWEST1 = "us-west1"
  final val USWEST2 = "us-west2"
  final val EUROPE = "europe"
  final val EUROPEWEST1 = "europe-west1"
  final val EUROPEWEST2 = "europe-west2"
  final val EUROPEWEST3 = "europe-west3"
  final val EUROPEWEST4 = "europe-west4"
  final val EUROPENORTH1 = "europe-north1"
  final val NORTHAMERICANORTHEAST1 = "northamerica-northeast1"
  final val ASIA = "asia"
  final val ASIAEAST = "asia-east"
  final val ASIAEAST1 = "asia-east1"
  final val ASIAEAST2 = "asia-east2"
  final val ASIANORTHEAST = "asia-northeast"
  final val ASIASOUTHEAST = "asia-southeast"
  final val AUSTRALIASOUTHEAST1 = "australia-southeast1"
  final val AUSTRALIA = "australia"
  final val SOUTHAMERICAEAST1 = "southamerica-east1"
  final val ASIASOUTH1 = "asia-south1"

  final val allRegions = Seq(
    Global,
    Us,
    Uscentral1,
    Useast1,
    Uswest4,
    Uswest1,
    Uswest2,
    Europe,
    Europewest1,
    Europewest2,
    Europewest3,
    Europewest4,
    Europenorth1,
    Northamericanortheast1,
    Asia,
    Asiaeast,
    Asiaeast1,
    Asiaeast2,
    Asianortheast,
    Asiasoutheast,
    Australiasoutheast1,
    Australia,
    Southamericaeast1,
    Asiasouth1
  )

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
  final val SSD_STRING = "SSD"
  final val HDD_STRING = "HDD"

  final val SSD_DESCRIPTION_STRING = "SSD"
  final val HDD_DESCRIPTION_STRING = "PD"

  final val SSD_RESOURCE_GROUP_STRING = "SSD"
  final val HDD_RESOURCE_GROUP_STRING = "PDStandard"

  final val allDiskTypes = Seq(SSD, HDD)

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
  final val RUNNING = "Running"
  final val NOTSTARTED = "NotStarted"
  final val STARTING = "Starting"
  final val FAILED = "Failed"
  final val DONE = "Done"

  final val allStatuses = Seq(Running, NotStarted, Starting, Failed, Done)
  final val inFlightStatuses = Seq(Running, NotStarted, Starting)
  final val terminalStatuses = Seq(Failed, Done)

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
  final val JES = "JES"
  final val PAPIV2 = "PAPIV2" //???

  final val allBackEnds = Seq(Jes, PapiV2)

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
