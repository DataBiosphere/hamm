package org.broadinstitute.workbench.hamm
package pricing

import cats.effect.Sync
import io.circe.Json
import org.broadinstitute.workbench.hamm.pricing.JsonCodec._
import org.http4s.{EntityDecoder, Uri}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client


final case class PriceList(region: Region,
                     machineType: MachineType,
                     ssdCostPerGbPerHour: Double,
                     hddCostPerGbPerHour: Double,
                     CPUOnDemandPrice: Double,
                     RAMOnDemandPrice: Double,
                     extendedOnDemandRAMPrice: Double,
                     CPUPreemptiblePrice: Double,
                     RAMPreemptiblePrice: Double,
                     extendedRAMPreemptiblePrice: Double)

case class GcpPriceList(asJson: Json) extends AnyVal

class GcpPricing[F[_]: Sync](httpClient: Client[F], uri: Uri) {

  def getGcpPriceList(region: Region, machineType: MachineType): F[PriceList] = {
    // F can not be flatmapped
//    for {
//      json <- httpClient.expect[Json](uri)
//      result <- JsonCodec.PriceListDecoder(region, machineType).decodeJson(json)
//    } yield result

    implicit val priceListDecoder = JsonCodec.PriceListDecoder(region, machineType)
    httpClient.expect[PriceList](uri)
  }
}


//CUSTOM_MACHINE_CPU = "CP-DB-PG-CUSTOM-VM-CORE"
//CUSTOM_MACHINE_RAM = "CP-DB-PG-CUSTOM-VM-RAM"
//CUSTOM_MACHINE_EXTENDED_RAM = "CP-COMPUTEENGINE-CUSTOM-VM-EXTENDED-RAM"
//CUSTOM_MACHINE_CPU_PREEMPTIBLE = "CP-COMPUTEENGINE-CUSTOM-VM-CORE-PREEMPTIBLE"
//CUSTOM_MACHINE_RAM_PREEMPTIBLE = "CP-COMPUTEENGINE-CUSTOM-VM-RAM-PREEMPTIBLE"
//CUSTOM_MACHINE_EXTENDED_RAM_PREEMPTIBLE = "CP-COMPUTEENGINE-CUSTOM-VM-EXTENDED-RAM-PREEMPTIBLE"
//CUSTOM_MACHINE_TYPES = [CUSTOM_MACHINE_CPU,
//CUSTOM_MACHINE_RAM,
//CUSTOM_MACHINE_EXTENDED_RAM,
//CUSTOM_MACHINE_CPU_PREEMPTIBLE,
//CUSTOM_MACHINE_RAM_PREEMPTIBLE,
//CUSTOM_MACHINE_EXTENDED_RAM_PREEMPTIBLE]