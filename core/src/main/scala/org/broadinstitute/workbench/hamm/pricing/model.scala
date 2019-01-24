package org.broadinstitute.workbench.hamm.pricing

import io.circe.Decoder
import org.broadinstitute.workbench.ccm.{Region}


sealed trait UsageType {
  def asString: String
}

object UsageType {
  final val PREEMPTIBLE = "Preemptible"
  final val ONDEMAND = "OnDemand"

  final val allUsageTypes = Seq(Preemptible, OnDemand)

  val stringToUsageType = Map(
    PREEMPTIBLE -> Preemptible,
    ONDEMAND -> OnDemand
  )

  case object Preemptible extends UsageType {
    def asString = PREEMPTIBLE
  }

  case object OnDemand extends UsageType {
    def asString = ONDEMAND
  }
}


final case class SkuName(asString: String) extends AnyVal
final case class SkuId(asString: String) extends AnyVal
final case class SkuDescription(asString: String) extends AnyVal
final case class ServiceDisplayName(asString: String) extends AnyVal
final case class ResourceFamily(asString: String) extends AnyVal
final case class ResourceGroup(asString: String) extends AnyVal
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


