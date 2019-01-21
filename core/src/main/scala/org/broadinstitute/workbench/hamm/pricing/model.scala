package org.broadinstitute.workbench.hamm.pricing

import io.circe.Decoder
import org.broadinstitute.workbench.ccm.Region

final case class CpuCost(asDouble: Double) extends AnyVal
final case class RamCost(asDouble: Double) extends AnyVal

final case class ComputeCost(cpuCost: CpuCost, ramCost: RamCost) {
  val totalCost = cpuCost.asDouble + ramCost.asDouble //TODO: update this
}

final case class SkuName(asString: String) extends AnyVal
final case class SkuId(asString: String) extends AnyVal
final case class SkuDescription(asString: String) extends AnyVal
final case class ServiceDisplayName(asString: String) extends AnyVal
final case class ResourceFamily(asString: String) extends AnyVal
final case class ResourceGroup(asString: String) extends AnyVal
final case class UsageType(asString: String) extends AnyVal
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


