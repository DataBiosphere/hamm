package org.broadinstitute.workbench.hamm


import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoField, Temporal, TemporalAccessor}
import java.text.SimpleDateFormat
import java.time.Instant

import cats.implicits._
import io.circe.{Decoder, DecodingFailure}

import scala.concurrent.duration.{Duration, FiniteDuration}

object JsonCodec {

  val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  implicit val cpuNumberDecoder: Decoder[CpuNumber] = Decoder.decodeString.emap(s => Either.catchNonFatal(s.toInt).leftMap(_.getMessage).map(CpuNumber))
  implicit val bootDiskSizeGbDecoder: Decoder[BootDiskSizeGb] = Decoder.decodeString.emap(x => Either.catchNonFatal(x.toInt).leftMap(_.getMessage).map(BootDiskSizeGb))

  implicit val diskNameDecoder: Decoder[Disks] = Decoder.decodeString.emap{
    str =>
      // sample value for str: `local-disk 1 HDD`
      for{
        array <- Either.catchNonFatal(str.split(" ")).leftMap(_.getMessage)
        size <- Either.catchNonFatal(array(1).toInt).leftMap(_.getMessage)
      } yield Disks(DiskName(array(0)), DiskSize(size), DiskType.stringToDiskType(array(2)))
  }


  implicit val runtimeAttributesDecoder: Decoder[RuntimeAttributes] = Decoder.instance {
    cursor =>
      for {
        cpuNumber <- cursor.downField("cpu").as[Int]
        disks <- cursor.downField("disks").as[Disks]
        bootDiskSizeGb <- cursor.downField("bootDiskSizeGb").as[Int]
        preemptibleAttemptsAllowed <- cursor.downField("preemptible").as[Int]
      } yield RuntimeAttributes(CpuNumber(cpuNumber), disks, BootDiskSizeGb(bootDiskSizeGb), PreemptibleAttemptsAllowed(preemptibleAttemptsAllowed))
  }

  implicit val executionEventDecoder: Decoder[ExecutionEvent] = Decoder.instance  { cursor =>

    for {
      description <- cursor.downField("description").as[String]
      startTime <- cursor.downField("startTime").as[Instant]
      endTime <- cursor.downField("endTime").as[Instant]
    } yield ExecutionEvent(ExecutionEventDescription(description), startTime, endTime)

  }

  implicit val callDecoder: Decoder[Call] = Decoder.instance {
    cursor =>
      for {
        ra <- cursor.downField("runtimeAttributes").as[RuntimeAttributes]
        executionEvents <- cursor.downField("executionEvents").as[List[ExecutionEvent]]
        isPreemptible <- cursor.downField("preemptible").as[Boolean]
        isCallCaching <- cursor.downField("callCaching").downField("hit").as[Boolean]
        zone <- cursor.downField("jes").downField("zone").as[String]
        region <- Either.catchNonFatal("-[a-z]\\z".r.replaceAllIn(zone,"")).leftMap(e => DecodingFailure(s"Could not obtain region from zone $zone", List()))
        machineTypeString <- cursor.downField("jes").downField("machineType").as[String]
        machineType <- Either.catchNonFatal(machineTypeString.split("/").last).leftMap(e => DecodingFailure(s"Could not obtain machine type from $machineTypeString", List()))
        status <- cursor.downField("executionStatus").as[String]
        backend <- cursor.downField("backend").as[String]
        attempt <- cursor.downField("attempt").as[Int]
      } yield Call(ra, executionEvents, isCallCaching, isPreemptible, Region.stringToRegion(region), Status.stringToStatus(status), MachineType.stringToMachineType(machineType), BackEnd.stringToBackEnd(backend), Attempt(attempt))
  }

  implicit val metadataResponseDecoder: Decoder[MetadataResponse] = Decoder.instance {
    cursor =>
      for {
        calls <- cursor.downField("calls").as[Map[String, List[Call]]].map(x => (x.values.flatten.toList))
        start <- cursor.downField("start").as[Instant]
        end <- cursor.downField("end").as[Instant]
      } yield MetadataResponse(calls, start, end)
  }


}