package org.broadinstitute.workbench.hamm.dao

import cats.effect.Sync
import cats.implicits._
import org.broadinstitute.workbench.hamm.model.GooglePriceListJsonCodec._
import org.broadinstitute.workbench.hamm.model._
import org.http4s.Uri
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client



class GooglePriceListDAO[F[_]: Sync](httpClient: Client[F], uri: Uri) {

  def getGcpPriceList(): F[GooglePriceList] = {
    for {
      googlePriceList <- httpClient.expect[GooglePriceList]("https://cloudbilling.googleapis.com/v1/services/6F81-5844-456A/skus?key=AIzaSyCWbeI33AMZINdULiZy1hS_gHGGZrF-nrs")
    } yield {
      googlePriceList
    }
  }
}


object GooglePriceListDAO {

  def parsePriceList(googlePriceList: GooglePriceList, computePriceKeys: List[ComputePriceKey], storagePriceKeys: List[StoragePriceKey]): Either[Throwable, PriceList] = {

    def getPrice(region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, descriptionShouldInclude: List[String], descriptionShouldNotInclude: List[String]): Either[String, Double] = {
      val sku = googlePriceList.priceItems.filter { priceItem =>
        (priceItem.regions.contains(region)
          && priceItem.category.resourceFamily.equals(resourceFamily)
          && priceItem.category.resourceGroup.equals(resourceGroup)
          && priceItem.category.usageType.equals(usageType)
          && descriptionShouldInclude.foldLeft(true)((a, b) => a && priceItem.description.asString.contains(b))
          && descriptionShouldNotInclude.foldLeft(true)((a, b) => a && !priceItem.description.asString.contains(b)))
      }
      sku.length match {
        case 0  => Left(s"No SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude.")
        case 1 => getPriceFromSku(sku.head)
        case tooMany => Left(s"$tooMany SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude. ${sku.toString}.")
      }
    }

    def getPriceFromSku(priceItem: GooglePriceItem): Either[String, Double] = {
      // ToDo: Currently just takes first, make it take either most recent or make it dependent on when the call ran
      priceItem.pricingInfo.headOption match {
        case None => Left(s"Price Item $priceItem had no pricing info")
        case Some(head) => Right(head.tieredRates.filter(rate => rate.startUsageAmount.asInt == 0).head.nanos.asInt.toDouble / 1000000000)
      }
    }

    def getComputePrices(computePriceKey: ComputePriceKey): Either[String, ComputePrices] = {
      for {
        cpuPrice <- getPrice(computePriceKey.region,
                            ResourceFamily.Compute,
                            ResourceGroup(computePriceKey.machineType.asCPUresourceGroupString),
                            computePriceKey.usageType,
                            List(computePriceKey.machineType.asDescriptionString, ResourceFamily.Compute.asDescriptionString),
                            List("CPU Upgrade Premium"))
        ramPrice <- getPrice(computePriceKey.region,
                            ResourceFamily.Compute,
                            ResourceGroup(computePriceKey.machineType.asRAMresourceGroupString),
                            computePriceKey.usageType,
                            List(computePriceKey.machineType.asDescriptionString, ResourceFamily.Storage.asDescriptionString),
                            List("CPU Upgrade Premium"))
      }  yield ComputePrices(cpuPrice, ramPrice)
    }

    def getStoragePrice(storagePriceKey: StoragePriceKey): Either[String, Double] = {
        getPrice(storagePriceKey.region, ResourceFamily.Storage, ResourceGroup(storagePriceKey.diskType.asResourceGroupString), UsageType.OnDemand, List(), List("Regional"))
    }

    val computePrices: Seq[Either[String, (ComputePriceKey, ComputePrices)]] = computePriceKeys.map{ key =>
      for {
        prices <- getComputePrices(key)
      } yield (key, prices)
    }

    val storagePrices = storagePriceKeys.map { key =>
      for {
        price <- getStoragePrice(key)
      } yield (key, price / (24 * 365 / 12)) // price we get is per month, we want per hour
    }

    for {
      computePricesParseq <- computePrices.toList.parSequence.leftMap(errors => new Exception(errors.toString)).map(x => ComputePriceList(x.toMap))
      storagePricesParseq <- storagePrices.toList.parSequence.leftMap(errors => new Exception(errors.toString)).map(x => StoragePriceList(x.toMap))
    } yield {
      val priceList = PriceList(computePricesParseq, storagePricesParseq)
      priceList
    }
  }

}
