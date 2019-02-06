package org.broadinstitute.workbench.hamm.model

import java.time.Instant
import java.util.UUID


sealed trait UsageType {
  def asString: String
  def asDescriptionString: String
}

object UsageType {
  private final val PREEMPTIBLE = "Preemptible"
  private final val ONDEMAND = "OnDemand"
  private final val COMMIT1YR = "Commit1Yr"

  private final val PREEMPTIBLE_DESCRIPTION_STRING = "Preemptible"
  private final val ONDEMAND_DESCRIPTION_STRING = ""
  private final val COMMIT1YR_DESCRIPTION_STRING = "Commitment v1:"

  val stringToUsageType = Map(
    PREEMPTIBLE -> Preemptible,
    ONDEMAND -> OnDemand,
    COMMIT1YR -> Commit1Yr
  )

  case object Preemptible extends UsageType {
    def asString = PREEMPTIBLE
    def asDescriptionString: String = PREEMPTIBLE_DESCRIPTION_STRING
  }

  case object OnDemand extends UsageType {
    def asString = ONDEMAND
    def asDescriptionString: String = ONDEMAND_DESCRIPTION_STRING
  }

  case object Commit1Yr extends UsageType {
    def asString = COMMIT1YR
    def asDescriptionString: String = COMMIT1YR_DESCRIPTION_STRING
  }
}

sealed trait ResourceFamily {
  def asString: String
}

object ResourceFamily {
  private final val COMPUTE_STRING = "Compute"
  private final val STORAGE_STRING = "Storage"


  val stringToResourceFamily = Map(
    COMPUTE_STRING -> Compute,
    STORAGE_STRING -> Storage
  )

  case object Compute extends ResourceFamily {
    def asString = COMPUTE_STRING
  }
  case object Storage extends ResourceFamily {
    def asString = STORAGE_STRING
  }
}



final case class SkuName(asString: String) extends AnyVal
final case class SkuId(asString: String) extends AnyVal
final case class SkuDescription(asString: String) extends AnyVal
final case class ServiceDisplayName(asString: String) extends AnyVal
final case class ResourceGroup(asString: String) extends AnyVal
final case class UsageUnit(asString: String) extends AnyVal
final case class StartUsageAmount(asInt: Int) extends AnyVal
final case class CurrencyCode(asString: String) extends AnyVal
final case class Units(asInt: Int) extends AnyVal
final case class Nanos(asInt: Int) extends AnyVal


final case class TieredRate(startUsageAmount: StartUsageAmount, currencyCode: CurrencyCode, units: Units, nanos: Nanos)
final case class PricingInfo(usageUnit: UsageUnit, tieredRates: List[TieredRate])
final case class Category(serviceDisplayName: ServiceDisplayName, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType)
final case class GooglePriceItem(name: SkuName, skuId: SkuId, description: SkuDescription, category: Category, regions: List[Region], pricingInfo: List[PricingInfo])
final case class GooglePriceList(priceItems: List[GooglePriceItem])


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
  private final val US_STRING = "us"
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
    US_STRING -> US,
    USCENTRAL1 -> UScentral1,
    USEAST1 -> USeast1,
    USWEST4 -> USwest4,
    USWEST1 -> USwest1,
    USWEST2 -> USwest2,
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
  case object US extends Region {
    def asString = US_STRING
  }
  case object UScentral1 extends Region {
    def asString = USCENTRAL1
  }
  case object USeast1 extends Region {
    def asString = USEAST1
  }
  case object USwest4 extends Region {
    def asString = USWEST4
  }
  case object USwest1 extends Region {
    def asString = USWEST1
  }
  case object USwest2 extends Region {
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

// Leaving this here for now - "backend" is something user specified so we can't be sure it'll
//   be either JES or PAPIV2 - currently we're calculating without taking PAPI versions into account
//   but in the future we might want to find a better way to differentiate these
object BackEnd {
  private final val JES = "JES"
  private final val PAPIV2 = "PAPIv2"

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
                                   disks: Disk,
                                   bootDiskSizeGb: BootDiskSizeGb,
                                   preemptibleAttemptsAllowed: PreemptibleAttemptsAllowed)

final case class Disk(diskName: DiskName, diskSize: DiskSize, diskType: DiskType)

final case class PriceList(compute: ComputePriceList, storage: StoragePriceList)

final case class ComputePriceList(computePrices: Map[ComputePriceKey, ComputePrices])
final case class ComputePriceKey(region: Region, machineType: MachineType, usageType: UsageType)
final case class ComputePrices(ram: Double, cpu: Double)

final case class StoragePriceList(pricesByDisk: Map[StoragePriceKey, Double])
final case class StoragePriceKey(region: Region, diskType: DiskType)