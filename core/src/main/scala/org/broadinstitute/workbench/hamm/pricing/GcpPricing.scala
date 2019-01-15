package org.broadinstitute.workbench.hamm
package pricing

import cats.effect.Sync
import io.circe.Json
import org.broadinstitute.workbench.hamm.pricing.JsonCodec._
import org.http4s.Uri
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client


case class PriceList(region: Region,
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

  def getGcpPriceList(): F[GcpPriceList] = {
    httpClient.expect[GcpPriceList](uri)
  }

  def getPriceList(region: Region, machineType: MachineType, googlePriceList: Skus): Either[Throwable, PriceList] = {

    def getPrice(resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, descriptionShouldInclude: Option[String], descriptionShouldNotInclude: Option[String]): Either[String, Double] = {
      val sku = googlePriceList.priceItems.filter { priceItem =>
        (priceItem.serviceRegions.contains(region)
          && priceItem.category.resourceFamily.equals(resourceFamily)
          && priceItem.category.resourceGroup.equals(resourceGroup)
          && priceItem.category.usageType.equals(usageType)
          && (descriptionShouldInclude match {
          case Some(desc) => priceItem.description.asString.contains(desc)
          case None => true})
          && (descriptionShouldNotInclude match {
          case Some(desc) => !priceItem.description.asString.contains(desc)
          case None => true}))
      }

      sku.length match {
        case 0  => Left(s"No SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude in the following price list: $googlePriceList")
        case 1 => Right(getPriceFromSku(sku.head))
        case tooMany => Left(s"$tooMany SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude in the following price list: $googlePriceList")
      }
    }

    val priceList = for {
      ssdCostPerGbPerMonth <- getPrice(ResourceFamily("Storage"), ResourceGroup("SSD"), UsageType("OnDemand"), None, Some("Regional"))
      hddCostPerGbPerMonth <- getPrice(ResourceFamily("Storage"), ResourceGroup("PdStandard"), UsageType("OnDemand"), None, Some("Regional"))
      cpuOnDemandCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("CPU"), UsageType("OnDemand"), None, None )
      ramOnDemandCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("OnDemand"), None, Some("Custom Extended"))
      extendedRamOnDemandCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("OnDemand"), Some("Custom Extended"), None)
      cpuPreemptibleCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("CPU"), UsageType("Preemptible"), None, Some("Custom Extended"))
      ramPreemptibleCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("Preemptible"), None, Some("Custom Extended"))
      extendedRamPreemptibleCostGibibytesPerHour <- getPrice(ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("Preemptible"), Some("Custom Extended"), None)
    } yield {
      val ssdCostPerGbPerHour = ssdCostPerGbPerMonth * (24 * 365 / 12)
      val hddCostPerGbPerHour = hddCostPerGbPerMonth * (24 * 365 / 12)
      PriceList(
        region,
        machineType,
        ssdCostPerGbPerHour,
        hddCostPerGbPerHour,
        cpuOnDemandCostGibibytesPerHour,
        ramOnDemandCostGibibytesPerHour,
        extendedRamOnDemandCostGibibytesPerHour,
        cpuPreemptibleCostGibibytesPerHour,
        ramPreemptibleCostGibibytesPerHour,
        extendedRamPreemptibleCostGibibytesPerHour
      )
    }
    priceList.left.map(errorString => new Exception(errorString))
  }

  private def getPriceFromSku(priceItem: GooglePriceItem): Double = {
    // ToDo: Currently just takes first, make it take either most recent or make it dependent on when the call ran
    priceItem.pricingInfo.head.tieredRates.filter(rate => rate.startUsageAmount.asInt == 0).head.nanos.asInt.toDouble * 1000000000
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