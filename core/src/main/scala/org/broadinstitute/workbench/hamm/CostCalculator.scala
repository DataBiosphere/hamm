package org.broadinstitute.workbench.hamm
import java.time.{Duration, Instant}

import cats.data.NonEmptyList
import cats.implicits._
import org.broadinstitute.workbench.hamm.pricing.{ComputePriceKey, PriceList, StoragePriceKey, UsageType}


object CostCalculator {

  def getPriceOfWorkflow(callMetaDataJson: MetadataResponse, priceList: PriceList): Either[Throwable, Double] = {
    val ls: List[Either[NonEmptyList[String], Double]] = callMetaDataJson.calls.map { call =>
      getPriceOfCall(call, priceList, Instant.now(), Instant.now()).leftMap(NonEmptyList.one)
    }

    ls.parSequence.leftMap(errors => new Exception(errors.toList.toString)).map(_.sum)
  }

  private def getPriceOfCall(call: Call, priceList: PriceList, startTime: Instant, endTime: Instant): Either[String, Double] = {
    for {
      _ <- if (Status.terminalStatuses.contains(call.status)) Right(()) else Left(s"Call {name} status was ${call.status.asString}.") // not evaluating calls that are in flight
      diskSize = call.runtimeAttributes.disks.diskSize.asInt + call.runtimeAttributes.bootDiskSizeGb.asInt
      diskType = call.runtimeAttributes.disks.diskType
      usageType = getUsageType(call)
      computePrices <- Either.catchNonFatal(priceList.compute.computePrices.get(ComputePriceKey(call.region, call.machineType, usageType)).get).leftMap(_ => s"couldn't get compute prices for region ${call.region}, machineType ${call.machineType}, $usageType and non-extended.")
      storagePrice <-  Either.catchNonFatal(priceList.storage.pricesByDisk.get(StoragePriceKey(call.region, diskType)).get).leftMap(_ => s"couldn't get storage prices for region ${call.region}, and diskType $diskType")
    } yield {
      // ToDo: calculate subworkflows
      val wasPreempted = wasCallPreempted(call)
      // only looking at actual and not requested disk info
      val callDurationInSeconds = getCallDuration(call, startTime, endTime)
      // adjust the call duration to account for preemptibility - if a VM preempted less than 10 minutes after it is created, user incurs no cost
      val adjustedCallDurationInSeconds = if (usageType == UsageType.Preemptible && wasPreempted && callDurationInSeconds < (10 * 60)) 0 else callDurationInSeconds
      val cpuCost = adjustedCallDurationInSeconds * computePrices.cpu
      val diskGbHours = call.runtimeAttributes.disks.diskSize.asInt * (adjustedCallDurationInSeconds)
      val diskCost = diskGbHours * storagePrice
      val memCost = adjustedCallDurationInSeconds * computePrices.ram
      cpuCost + diskCost + memCost
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

  private def getCallDuration(call: Call, cromwellStartTime: Instant, cromwellEndTime: Instant): Long = {
    val papiV2 = call.backend.asString.equals("PAPIv2")

    def getCromwellStart =  {
      call.executionEvents.find(event => event.description.asString.equals("start")) match {
        case Some(nonPapiV2Event) => nonPapiV2Event.startTime
        case None => cromwellStartTime
      }
    }

    def getCromwellEnd =  {
      call.executionEvents.find(event => event.description.asString.equals("ok")) match {
        case Some(nonPapiV2Event) => nonPapiV2Event.endTime
        case None => cromwellEndTime
      }
    }

    val startTime = if (papiV2) {
      val startOption = call.executionEvents.find(event => event.description.asString.contains("Preparing Job"))
      startOption match {
        case Some(event) => event.startTime
        case None => getCromwellStart
      }
    } else getCromwellStart

    val endTime = if (papiV2) {
      val endOption = call.executionEvents.find(event => event.description.asString.contains("Worker Released"))
      endOption match {
        case Some(event) => event.endTime
        case None => getCromwellEnd
      }
    } else getCromwellEnd

    val elapsed = Duration.between(startTime, endTime).getSeconds
    if (elapsed >= 60) elapsed else 60
  }
}
