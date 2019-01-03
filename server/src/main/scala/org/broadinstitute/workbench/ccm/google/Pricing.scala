package org.broadinstitute.workbench.ccm
package google

import cats.effect.Sync
import io.circe.{Decoder, Json}
import org.broadinstitute.workbench.ccm.google.Pricing.gcpPriceListDecoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client

class Pricing[F[_]: Sync](httpClient: Client[F]) {
  def getPriceList(): F[GcpPriceList] = {
    httpClient.expect[GcpPriceList]("https://cloudpricingcalculator.appspot.com/static/data/pricelist.json")
  }
}

//TODO: this needs to be updated to use https://cloud.google.com/billing/v1/how-tos/catalog-api
object Pricing {
  implicit val cpuDecoder: Decoder[CpuCost] = Decoder.forProduct1("us")(CpuCost.apply)
  implicit val ramDecoder: Decoder[RamCost] = Decoder.forProduct1("us")(RamCost.apply)
  implicit val computeCostDecoder: Decoder[ComputeCost] = Decoder.forProduct2("CP-DB-PG-CUSTOM-VM-CORE", "CP-DB-PG-CUSTOM-VM-RAM")(ComputeCost.apply)
  implicit val gcpPriceListDecoder: Decoder[GcpPriceList] = Decoder.forProduct1("gcp_price_list")(GcpPriceList.apply)
}

final case class CpuCost(asDouble: Double) extends AnyVal
final case class RamCost(asDouble: Double) extends AnyVal

final case class ComputeCost(cpuCost: CpuCost, ramCost: RamCost) {
  val totalCost = cpuCost.asDouble + ramCost.asDouble //TODO: update this
}
final case class GcpPriceList(asJson: Json) extends AnyVal

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