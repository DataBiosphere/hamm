package org.broadinstitute.workbench.hamm.pricing

import cats.data.NonEmptyList
import io.circe.Decoder
import org.broadinstitute.workbench.hamm.Region


sealed trait UsageType {
  def asString: String
  def asDescriptionString: String
}

object UsageType {
  final val PREEMPTIBLE = "Preemptible"
  final val ONDEMAND = "OnDemand"
  final val COMMIT1YR = "Commit1Yr"

  final val PREEMPTIBLE_DESCRIPTION_STRING = "Preemptible"
  final val ONDEMAND_DESCRIPTION_STRING = ""
  final val COMMIT1YR_DESCRIPTION_STRING = "Commitment v1:"


  final val allUsageTypes = Seq(Preemptible, OnDemand, Commit1Yr)

  val stringToUsageType = Map(
    PREEMPTIBLE -> Preemptible,
    ONDEMAND -> OnDemand,
    COMMIT1YR -> Commit1Yr
  )

  case object Preemptible extends UsageType {
    def asString = PREEMPTIBLE
    def asDescriptionString: String = PREEMPTIBLE_DESCRIPTION_STRING
  }

  case object OnDemand extends UsageType {
    def asString = ONDEMAND
    def asDescriptionString: String = ONDEMAND_DESCRIPTION_STRING
  }

  case object Commit1Yr extends UsageType {
    def asString = COMMIT1YR
    def asDescriptionString: String = COMMIT1YR_DESCRIPTION_STRING
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
final case class GooglePriceList(priceItems: List[GooglePriceItem]) {

  def filterByResourceFamily(resourceFamilies: NonEmptyList[String]): GooglePriceList = {
    GooglePriceList(this.priceItems.filter(priceItems => resourceFamilies.map(resourceFamily=> priceItems.category.resourceFamily.asString.equals(resourceFamily))
      .reduce((a:Boolean, b:Boolean) => a||b)))
  }
}


