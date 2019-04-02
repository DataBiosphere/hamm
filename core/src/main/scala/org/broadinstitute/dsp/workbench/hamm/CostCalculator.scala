package org.broadinstitute.dsp.workbench.hamm

import java.time.{Duration, Instant}
import java.util.concurrent.TimeUnit

import cats.data.NonEmptyList
import cats.implicits._
import org.broadinstitute.dsp.workbench.hamm.db.{DbReference, PricingCalculatorPriceTable, PricingCalculatorPriceUniqueKey, SKUPriceTable}
import org.broadinstitute.dsp.workbench.hamm.model._

import scala.concurrent.duration.FiniteDuration

class CostCalculator(dbRef: DbReference, skuPriceTable: SKUPriceTable, pricingCalculatorPriceTable: PricingCalculatorPriceTable) {


  def getPriceOfWorkflow(workflowMetaDataJson: MetadataResponse): Double = {
    val ls: List[Either[NonEmptyList[String], Double]] = workflowMetaDataJson.calls.map { call =>
      getPriceOfCall(call, workflowMetaDataJson.startTime, workflowMetaDataJson.endTime).leftMap(NonEmptyList.one)
    }

    val thing: Either[Throwable, Double] = ls.parSequence.leftMap(errors => new Exception(errors.toList.toString)).map(_.sum)

    thing match {
      case Left(throwable) => throw throwable
      case Right(sum) => sum
    }
  }



  private def getPriceOfCall(call: Call, startTime: Instant, endTime: Instant): Either[String, Double] = {
    for {
      _ <- if (Status.terminalStatuses.contains(call.status)) Right(()) else Left(s"Call {name} status was ${call.status.asString}.") // not evaluating calls that are in flight
      // ToDo: handle multiple disks in one call
    } yield {
      // ToDo: calculate subworkflows
      val usageType = getUsageType(call)
      val adjustedCallDurationInSeconds = getAdjustedCallDurationInSeconds(call, usageType, startTime, endTime)
      val computeCost = getComputeCost(call, adjustedCallDurationInSeconds, usageType, startTime)
      val storageCost = getStorageCost(call, adjustedCallDurationInSeconds, startTime)

      computeCost + storageCost
    }
  }

  private def getAdjustedCallDurationInSeconds(call: Call, usageType: UsageType, startTime: Instant, endTime: Instant): Long = {
    val wasPreempted = wasCallPreempted(call)
    val callDuration = getCallDuration(call, startTime, endTime)   // only looking at actual and not requested disk info
    if (usageType == UsageType.Preemptible && wasPreempted && callDuration.toSeconds < (10 * 60)) 0 else callDuration.toSeconds // adjust the call duration to account for preemptibility - if a VM preempted less than 10 minutes after it is created, user incurs no cost
  }

  private def getStorageCost(call: Call, adjustedCallDurationInSeconds: Long, startTime: Instant): Double = {
    val storagePrice = getStoragePrice(call, startTime)
    val diskSize = call.runtimeAttributes.disks.diskSize.asInt + call.runtimeAttributes.bootDiskSizeGb.asInt
    val diskGbHours = diskSize * (adjustedCallDurationInSeconds)
    diskGbHours * storagePrice
  }

  private def getComputeCost(call: Call, adjustedCallDurationInSeconds: Long, usageType: UsageType, startTime: Instant): Double = {
    if (call.machineType.equals(MachineType.Custom)) {
      val computePrices = getCustomComputePrice(call, usageType, startTime)
      val cpuCost = adjustedCallDurationInSeconds * computePrices.cpu
      val memCost = adjustedCallDurationInSeconds * computePrices.ram
      cpuCost + memCost
    } else {
      val computePrice = getNonCustomComputePrice(call, usageType, startTime)
      adjustedCallDurationInSeconds * computePrice
    }
  }

  private def getCustomBaseName(machineType: MachineType, extended: Boolean): String = {
    "CP-COMPUTEENGINE-"+
      machineType.asMetadataString +
      "-VM" +
      (if (extended) "-EXTENDED" else "")
  }

  private def getNonCustomName(machineType: MachineType, usageType: UsageType): String = {
    "CP-COMPUTEENGINE-VMIMAGE" +
      machineType.asMetadataString +
      usageType.asPricingCalculatorString
  }


  private def getStorageName(diskType: DiskType): String = {
    "CP-COMPUTEENGINE-STORAGE-" +
       diskType.asPricingCalculatorString
  }


  private def getCustomCoreName(machineType: MachineType, usageType: UsageType, extended: Boolean): String = {
    getCustomBaseName(machineType: MachineType, extended: Boolean) + "-CORE" + usageType.asPricingCalculatorString
  }

  private def getCustomRAMName(machineType: MachineType, usageType: UsageType, extended: Boolean): String = {
    getCustomBaseName(machineType: MachineType, extended: Boolean) + "-RAM" + usageType.asPricingCalculatorString
  }


  private def getCustomComputePrice(call: Call, usageType: UsageType, startTime: Instant): ComputePrices = {
    val corePrice = getPrice(getCustomCoreName(call.machineType, usageType, false), call.region, ResourceFamily.Compute, ResourceGroup("CPU"), usageType, startTime)
    val ramPrice  = getPrice(getCustomRAMName(call.machineType, usageType, false), call.region, ResourceFamily.Compute, ResourceGroup("RAM "), usageType, startTime)
    ComputePrices(corePrice, ramPrice)
  }

  private def getNonCustomComputePrice(call: Call, usageType: UsageType, startTime: Instant): Double = {
    getPrice(getNonCustomName(call.machineType, usageType), call.region, ResourceFamily.Compute, ResourceGroup(call.machineType.asString), usageType, startTime)
  }

  private def getStoragePrice(call: Call, startTime: Instant): Double = {
    getPrice(getStorageName(call.runtimeAttributes.disks.diskType), call.region, ResourceFamily.Storage, ResourceGroup(call.runtimeAttributes.disks.diskType.asResourceGroupString), UsageType.OnDemand, startTime)
  }

  private def getPrice(name: String, region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, startTime: Instant): Double = {
    dbRef.inReadOnlyTransaction { implicit session =>
      skuPriceTable.getPriceRecordQuery(region, resourceFamily, resourceGroup, usageType, startTime) match {
        case Some(skuPrice: Double) => skuPrice
        case _ => pricingCalculatorPriceTable.getPriceQuery(PricingCalculatorPriceUniqueKey(name, startTime), region) match {
          case Some(pricingCalculatorPrice: Double) => pricingCalculatorPrice
          case _ => throw HammException(404, "Price not found")
        }
      }
    }
  }

  private def wasCallPreempted(call: Call): Boolean = {
    // treat preempted and retryableFailure as the same
    call.executionEvents.exists(event => (event.description.asString.equals("Preempted") || event.description.asString.equals("RetryableFailure")))
  }

  private def getUsageType(call: Call): UsageType = {
    if (call.attempt.asInt <= call.runtimeAttributes.preemptibleAttemptsAllowed.asInt)
      UsageType.Preemptible else UsageType.OnDemand
  }

  private def getCallDuration(call: Call, cromwellStartTime: Instant, cromwellEndTime: Instant): FiniteDuration = {
    //val papiV2 = call.backend.asString.equals("PAPIv2")

    lazy val getCromwellStart =  {
      call.executionEvents.find(event => event.description.asString.equals("start")) match {
        case Some(nonPapiV2Event) => nonPapiV2Event.startTime
        case None => cromwellStartTime
      }
    }

    lazy val getCromwellEnd =  {
      call.executionEvents.find(event => event.description.asString.equals("ok")) match {
        case Some(nonPapiV2Event) => nonPapiV2Event.endTime
        case None => cromwellEndTime
      }
    }

    val startTime = call.executionEvents.find(event => event.description.asString.contains("Preparing Job")) match {
        case Some(event) => event.startTime
        case None => getCromwellStart
      }

    val endTime = call.executionEvents.find(event => event.description.asString.contains("Worker Released")) match {
        case Some(event) => event.endTime
        case None => getCromwellEnd
      }

    val elapsed = Duration.between(startTime, endTime).getSeconds
    val seconds = if (elapsed >= 60) elapsed else 60

    FiniteDuration.apply(seconds, TimeUnit.SECONDS)
  }
}
