package org.broadinstitute.workbench.hamm.model

import java.time.Instant
import cats.implicits._
import io.circe.Decoder

object CromwellMetadataJsonCodec {
  implicit val cpuNumberDecoder: Decoder[CpuNumber] = Decoder.decodeString.emap(s => Either.catchNonFatal(s.toInt).leftMap(_.getMessage).map(CpuNumber))
  implicit val bootDiskSizeGbDecoder: Decoder[BootDiskSizeGb] = Decoder.decodeString.emap(x => Either.catchNonFatal(x.toInt).leftMap(_.getMessage).map(BootDiskSizeGb))


  implicit val runtimeAttributesDecoder: Decoder[RuntimeAttributes] = Decoder.instance {
    cursor =>
      for {
        cpuNumber      <- cursor.downField("cpu").as[Int]
        disks          <- cursor.downField("disks").as[Disk]
        bootDiskSizeGb <- cursor.downField("bootDiskSizeGb").as[Int]
        preemptibleAttemptsAllowed <- cursor.downField("preemptible").as[Int]
      } yield RuntimeAttributes(CpuNumber(cpuNumber), disks, BootDiskSizeGb(bootDiskSizeGb), PreemptibleAttemptsAllowed(preemptibleAttemptsAllowed))
  }

  implicit val diskDecoder: Decoder[Disk] = Decoder.decodeString.emap{
    str =>
      // sample value for str: `local-disk 1 HDD`
      for{
        array <- Either.catchNonFatal(str.split(" ")).leftMap(_.getMessage)
        size  <- Either.catchNonFatal(array(1).toInt).leftMap(_.getMessage)
      } yield Disk(DiskName(array(0)), DiskSize(size), DiskType.stringToDiskType(array(2)))
  }

  implicit val executionEventDecoder: Decoder[ExecutionEvent] = Decoder.instance  { cursor =>

    for {
      description <- cursor.downField("description").as[String]
      startTime   <- cursor.downField("startTime").as[Instant]
      endTime     <- cursor.downField("endTime").as[Instant]
    } yield ExecutionEvent(ExecutionEventDescription(description), startTime, endTime)

  }

  implicit val callDecoder: Decoder[Call] = Decoder.instance {
    cursor =>
      for {
        ra              <- cursor.downField("runtimeAttributes").as[RuntimeAttributes]
        executionEvents <- cursor.downField("executionEvents").as[List[ExecutionEvent]]
        isPreemptible   <- cursor.downField("preemptible").as[Boolean]
        isCallCaching   <- cursor.downField("callCaching").downField("hit").as[Boolean]
        region          <- cursor.downField("jes").downField("zone").as[Region]
        machineType     <- cursor.downField("jes").downField("machineType").as[MachineType]
        status          <- cursor.downField("executionStatus").as[Status]
        backend         <- cursor.downField("backend").as[BackEnd]
        attempt         <- cursor.downField("attempt").as[Int]
      } yield Call(ra, executionEvents, isCallCaching, isPreemptible, region, status, machineType, backend, Attempt(attempt))
  }

  implicit val statusDecoder: Decoder[Status] = Decoder.decodeString.emap{
    status => Status.stringToStatus.get(status).toRight(s"$status not a valid status")
  }

  implicit val machineTypeDecoder: Decoder[MachineType] = Decoder.decodeString.emap {
    machineTypeString =>
      Either.catchNonFatal(machineTypeString.split("/").last) match {
        case Left(t) => Left(s"Could not obtain machine type from $machineTypeString")
        case Right(machineType) => MachineType.stringToMachineType.get(machineType).toRight(s"$machineType is not a valid machine type")
      }
  }

  private implicit val regionDecoder: Decoder[Region] = Decoder.decodeString.emap {
    zone =>
      Either.catchNonFatal("-[a-z]\\z".r.replaceAllIn(zone,"")) match {
        case Left(t) => Left(s"Could not obtain region from zone $zone")
        case Right(region) => Region.stringToRegion.get(region).toRight(s"Region $region obtained from $zone is not a valid region.")
      }
  }

  implicit val backEndDecoder: Decoder[BackEnd] = Decoder.decodeString.emap {
    backEnd => BackEnd.stringToBackEnd.get(backEnd).toRight(s"$backEnd is not a valid back end")
  }

  implicit val metadataResponseDecoder: Decoder[MetadataResponse] = Decoder.instance {
    cursor =>
      for {
        calls  <- cursor.downField("calls").as[Map[String, List[Call]]].map(x => (x.values.flatten.toList))
        start  <- cursor.downField("start").as[Instant]
        end    <- cursor.downField("end").as[Instant]
        labels <- cursor.downField("labels").as[Map[String, String]]
      } yield MetadataResponse.apply(calls, start, end, labels)
  }
}