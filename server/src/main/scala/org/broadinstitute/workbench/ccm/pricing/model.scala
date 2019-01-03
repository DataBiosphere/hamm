package org.broadinstitute.workbench.ccm.pricing

import io.circe.Decoder

final case class CpuCost(asDouble: Double) extends AnyVal
final case class RamCost(asDouble: Double) extends AnyVal

final case class ComputeCost(cpuCost: CpuCost, ramCost: RamCost) {
  val totalCost = cpuCost.asDouble + ramCost.asDouble //TODO: update this
}

//TODO: this needs to be updated to use https://cloud.google.com/billing/v1/how-tos/catalog-api
object model {
  implicit val cpuDecoder: Decoder[CpuCost] = Decoder.forProduct1("us")(CpuCost.apply)
  implicit val ramDecoder: Decoder[RamCost] = Decoder.forProduct1("us")(RamCost.apply)
  implicit val computeCostDecoder: Decoder[ComputeCost] = Decoder.forProduct2("CP-DB-PG-CUSTOM-VM-CORE", "CP-DB-PG-CUSTOM-VM-RAM")(ComputeCost.apply)
  implicit val gcpPriceListDecoder: Decoder[GcpPriceList] = Decoder.forProduct1("gcp_price_list")(GcpPriceList.apply)
}