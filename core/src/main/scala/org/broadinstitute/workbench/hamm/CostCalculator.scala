package org.broadinstitute.workbench.ccm

import java.time.{Duration, Instant}

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.effect._
import cats.implicits._
import cats.data._
import org.broadinstitute.workbench.ccm.CostCalculator.preemptible
import org.broadinstitute.workbench.ccm.pricing.{GcpPriceList, PriceList, Skus}


object CostCalculator {

  def getPriceOfCall(callMetaDataJson: MetadataResponse, priceList: PriceList): Either[Throwable, Double] = {
    val ls: List[Either[NonEmptyList[String], Double]] = callMetaDataJson.calls.map { call =>
      getPriceOfCall(call, priceList, Instant.now(), Instant.now()).leftMap(NonEmptyList.one)
    }

    ls.parSequence.leftMap(errors => new Exception(errors.toList.mkString(", "))).map(_.sum)
  }

  private def getPriceOfCall(call: Call, priceList: PriceList, startTime: Instant, endTime: Instant): Either[String, Double] = {
    for {
      _ <- if (call.status.asString == "Success") Right(()) else Left(s"Call {name} status was ${call.status.asString}.") // not evaluating workflows that are in flight or Failed or Aborted or whatever
      machineType <- if (call.machineType.asString.contains("custom")) Right("custom") else {
        Either.catchNonFatal(call.machineType.asString.split("/", 1)).leftMap(_ => "MachineType could not be parsed.")}
    } yield {
      // ToDo: calculate subworkflows
      val isVMPreemptible = preemptible(call)
      val wasPreempted = wasCallPreempted(call)
      // only looking at actual and not requested disk info
      val diskName = call.runtimeAttributes.disks.diskName
      val diskSize = call.runtimeAttributes.disks.diskSize.asInt + call.runtimeAttributes.bootDiskSizeGb.asInt
      val diskType = call.runtimeAttributes.disks.diskType
      val callDurationInSeconds = getCallDuration(call, startTime, endTime)

      // ToDo: add calculating prices for non-custom
      // adjust the call duration to account for preemptibility
      // if a VM preempted less than 10 minutes after it is created, user incurs no cost
      val adjustedCallDurationInSeconds = if (isVMPreemptible && wasPreempted && callDurationInSeconds < (10 * 60)) 0 else callDurationInSeconds
      val cpuCost = adjustedCallDurationInSeconds * (if (isVMPreemptible) priceList.CPUPreemptiblePrice else priceList.CPUOnDemandPrice)
      val diskCostPerGbHour = if (call.runtimeAttributes.disks.diskType.asString.equals("SSD")) priceList.ssdCostPerGbPerHour else priceList.hddCostPerGbPerHour
      val diskGbHours = call.runtimeAttributes.disks.diskSize.asInt * (adjustedCallDurationInSeconds)
      val diskCost = diskGbHours * diskCostPerGbHour
      val memCost = adjustedCallDurationInSeconds * (if (isVMPreemptible) priceList.RAMPreemptiblePrice else priceList.RAMOnDemandPrice)
      cpuCost + diskCost + memCost
    }
  }


  private def wasCallPreempted(call: Call): Boolean = {
    // treat preempted and retryableFailure as the same
    call.executionEvents.exists(event => (event.description.asString.equals("Preempted") || event.description.asString.equals("RetryableFailure")))
  }

  private def preemptible(call: Call): Boolean = {
    call.attempt.asInt <= call.runtimeAttributes.preemptible.asInt
    // ToDo: Add false result if the metadata does not contain an "attempt" or preemptible info
  }

  private def getCallDuration(call: Call, cromwellStartTime: Instant, cromwellEndTime: Instant): Long = {
    // ToDo: add option to ignore preempted calls and just return 0
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
