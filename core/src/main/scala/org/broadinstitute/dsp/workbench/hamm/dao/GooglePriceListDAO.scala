package org.broadinstitute.dsp.workbench.hamm.dao

import cats.effect.IO
import org.broadinstitute.dsp.workbench.hamm.config.GoogleConfig
import org.broadinstitute.dsp.workbench.hamm.model._
import org.broadinstitute.dsp.workbench.hamm.db.PriceRecord
import org.broadinstitute.dsp.workbench.hamm.model.GoogleCloudPricingCalculatorJsonCodec._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client



class GooglePriceListDAO(httpClient: Client[IO], config: GoogleConfig) {

  def getPriceRecords(): List[PriceRecord] = {
    httpClient.expect[List[PriceRecord]](config.googleDefaultPricingUrl).unsafeRunSync()
  }

}


object GooglePriceListDAO {

  def parsePriceList(googlePriceList: GooglePriceList, computePriceKeys: List[ComputePriceKey], storagePriceKeys: List[StoragePriceKey]): PriceList = {

    def getPrice(region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, descriptionShouldInclude: List[String], descriptionShouldNotInclude: List[String]): Double = {
      val sku = googlePriceList.priceItems.filter { priceItem =>
        (priceItem.regions.contains(region)
          && priceItem.category.resourceFamily.equals(resourceFamily)
          && priceItem.category.resourceGroup.equals(resourceGroup)
          && priceItem.category.usageType.equals(usageType)
          && descriptionShouldInclude.foldLeft(true)((a, b) => a && priceItem.description.asString.contains(b))
          && descriptionShouldNotInclude.foldLeft(true)((a, b) => a && !priceItem.description.asString.contains(b)))
      }
      sku.length match {
        case 0  => throw HammException(404, s"No SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude.")
        case 1 => getPriceFromSku(sku.head)
        case tooMany => throw new Exception(s"$tooMany SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude. ${sku.toString}.")
      }
    }

    def getPriceFromSku(priceItem: GooglePriceItem): Double = {
      // ToDo: Currently just takes first, make it take either most recent or make it dependent on when the call ran
      priceItem.pricingInfo.headOption match {
        case None => throw new Exception(s"Price Item $priceItem had no pricing info")
        case Some(head) => head.tieredRates.filter(rate => rate.startUsageAmount.asInt == 0).head.nanos.asInt.toDouble / 1000000000
      }
    }

    def getComputePrices(computePriceKey: ComputePriceKey): ComputePrices = {
      val cpuPrice = getPrice(computePriceKey.region,
                              ResourceFamily.Compute,
                              ResourceGroup(computePriceKey.machineType.asCPUresourceGroupString),
                              computePriceKey.usageType,
                              List(computePriceKey.machineType.asDescriptionString, ResourceFamily.Compute.asDescriptionString),
                              List("CPU Upgrade Premium"))

      val ramPrice = getPrice(computePriceKey.region,
                              ResourceFamily.Compute,
                              ResourceGroup(computePriceKey.machineType.asRAMresourceGroupString),
                              computePriceKey.usageType,
                              List(computePriceKey.machineType.asDescriptionString, ResourceFamily.Storage.asDescriptionString),
                              List("CPU Upgrade Premium"))

      ComputePrices(cpuPrice, ramPrice)
    }

    def getStoragePrice(storagePriceKey: StoragePriceKey): Double = {
        getPrice(storagePriceKey.region, ResourceFamily.Storage, ResourceGroup(storagePriceKey.diskType.asResourceGroupString), UsageType.OnDemand, List(), List("Regional"))
    }

    val computePrices: List[(ComputePriceKey, ComputePrices)] = computePriceKeys.map{ key => (key, getComputePrices(key))}

    // price we get is per month, we want per hour
    val storagePrices: List[(StoragePriceKey, Double)] = storagePriceKeys.map { key => (key, getStoragePrice(key) / (24 * 365 / 12))}

    PriceList(ComputePriceList(computePrices.toMap), StoragePriceList(storagePrices.toMap))
  }

}
