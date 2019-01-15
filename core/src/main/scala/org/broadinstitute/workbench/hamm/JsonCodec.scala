package org.broadinstitute.workbench.hamm


import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoField, Temporal, TemporalAccessor}
import java.text.SimpleDateFormat
import cats.implicits._
import io.circe.Decoder

import scala.concurrent.duration.{Duration, FiniteDuration}

object JsonCodec {

  val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
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

  implicit val executionEventDecoder: Decoder[ExecutionEvent] = Decoder.instance  { cursor =>

    for {
      description <- cursor.downField("description").as[String]
      startTime <- cursor.downField("startTime").as[String]
      endTime <- cursor.downField("endTime").as[String]
    } yield ExecutionEvent(ExecutionEventDescription(description), formatter.parse(startTime).toInstant, formatter.parse(endTime).toInstant)

  }

  implicit val callDecoder: Decoder[Call] = Decoder.instance {
    cursor =>
      for {
        ra <- cursor.downField("runtimeAttributes").as[RuntimeAttributes]
        executionEvents <- cursor.downField("executionEvents").as[List[ExecutionEvent]]
        isPreemptible <- cursor.downField("preemptible").as[Boolean]
        isCallCaching <- cursor.downField("callCaching").downField("hit").as[Boolean]
        region <- cursor.downField("jes").downField("zone").as[String]
        machineType <- cursor.downField("jes").downField("machineType").as[String]
        status <- cursor.downField("backendStatus").as[String]
        backend <- cursor.downField("backend").as[String]
        attempt <- cursor.downField("attempt").as[Int]
      } yield Call(ra, executionEvents, isCallCaching, isPreemptible, Region(region), Status(status), MachineType(machineType), BackEnd(backend), Attempt(attempt))
  }

  implicit val metadataResponseDecoder: Decoder[MetadataResponse] = Decoder.instance {
    cursor =>
      for {
        calls <- cursor.downField("calls").as[Map[String, List[Call]]].map(x => (x.values.flatten.toList))
        start <- cursor.downField("start").as[String]
        end <- cursor.downField("end").as[String]
      } yield MetadataResponse(calls, formatter.parse(start).toInstant, formatter.parse(end).toInstant)
  }


}