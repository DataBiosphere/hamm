package org.broadinstitute.workbench.hamm.pricing

import org.broadinstitute.workbench.hamm.Region


sealed trait UsageType {
  def asString: String
  def asDescriptionString: String
}

object UsageType {
  private final val PREEMPTIBLE = "Preemptible"
  private final val ONDEMAND = "OnDemand"
  private final val COMMIT1YR = "Commit1Yr"

  private final val PREEMPTIBLE_DESCRIPTION_STRING = "Preemptible"
  private final val ONDEMAND_DESCRIPTION_STRING = ""
  private final val COMMIT1YR_DESCRIPTION_STRING = "Commitment v1:"

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

sealed trait ResourceFamily {
  def asString: String
}

object ResourceFamily {
  private final val COMPUTE_STRING = "Compute"
  private final val STORAGE_STRING = "Storage"


  val stringToResourceFamily = Map(
    COMPUTE_STRING -> Compute,
    STORAGE_STRING -> Storage
  )

  case object Compute extends ResourceFamily {
    def asString = COMPUTE_STRING
  }
  case object Storage extends ResourceFamily {
    def asString = STORAGE_STRING
  }
}



final case class SkuName(asString: String) extends AnyVal
final case class SkuId(asString: String) extends AnyVal
final case class SkuDescription(asString: String) extends AnyVal
final case class ServiceDisplayName(asString: String) extends AnyVal
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


