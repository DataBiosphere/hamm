package org.broadinstitute.workbench.hamm
package pricing

import io.circe.{Decoder, DecodingFailure}

//TODO: this needs to be updated to use https://cloud.google.com/billing/v1/how-tos/catalog-api
object JsonCodec {
  implicit val cpuDecoder: Decoder[CpuCost] = Decoder.forProduct1("us")(CpuCost.apply)
  implicit val ramDecoder: Decoder[RamCost] = Decoder.forProduct1("us")(RamCost.apply)
  implicit val computeCostDecoder: Decoder[ComputeCost] = Decoder.forProduct2("CP-DB-PG-CUSTOM-VM-CORE", "CP-DB-PG-CUSTOM-VM-RAM")(ComputeCost.apply)
  implicit val gcpPriceListDecoder: Decoder[GcpPriceList] = Decoder.forProduct1("gcp_price_list")(GcpPriceList.apply)


  // implicit val categoryDecoder: Decoder[Category] = Decoder.forProduct4("serviceDisplayName", "resourceFamily", "resourceGroup", "usageType")(Category.apply)
  implicit val categoryDecoder: Decoder[Category] = Decoder.instance { cursor =>
    for {
      serviceDisplayName <- cursor.downField("serviceDisplayName").as[String]
      resourceFamily <- cursor.downField("resourceFamily").as[String]
      resourceGroup <- cursor.downField("resourceGroup").as[String]
      usageType <- cursor.downField("usageType").as[String]
    } yield Category(ServiceDisplayName(serviceDisplayName), ResourceFamily(resourceFamily), ResourceGroup(resourceGroup), UsageType(usageType))
  }

  implicit val tieredRateDecoder: Decoder[TieredRate] = Decoder.instance { cursor =>
    for {
      startUsageAmount <- cursor.downField("startUsageAmount").as[Int]
      currencyCode <- cursor.downField("unitPrice").downField("currencyCode").as[String]
      units <- cursor.downField("unitPrice").downField("units").as[Int]
      nanos <- cursor.downField("unitPrice").downField("nanos").as[Int]
    } yield TieredRate(StartUsageAmount(startUsageAmount), CurrencyCode(currencyCode), Units(units), Nanos(nanos))
  }

  implicit val pricingInfoDecoder: Decoder[PricingInfo] = Decoder.instance { cursor =>
    for {
      usageUnit <- cursor.downField("pricingExpression").downField("usageUnit").as[String]
      tieredRates <- cursor.downField("pricingExpression").downField("tieredRates").as[List[TieredRate]]
    } yield PricingInfo(UsageUnit(usageUnit), tieredRates)
  }

  implicit val googlePriceItemDecoder: Decoder[GooglePriceItem] = Decoder.instance {
    // put filtering in here!
    cursor =>
      for {
        name <- cursor.downField("name").as[String]
        skuId <- cursor.downField("skuId").as[String]
        description <- cursor.downField("description").as[String]
        category <- cursor.downField("category").as[Category]
        regions <- cursor.downField("serviceRegions").as[List[String]]
        pricingInfo <- cursor.downField("pricingInfo").as[List[PricingInfo]]
      } yield GooglePriceItem(SkuName(name), SkuId(skuId), SkuDescription(description), category, regions.map(Region(_)), pricingInfo)
  }

  def PriceListDecoder(region: Region, machineType: MachineType): Decoder[PriceList] = Decoder.instance  {
    cursor =>
      def getPrice(googlePriceItems: List[GooglePriceItem], resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, descriptionShouldInclude: Option[String], descriptionShouldNotInclude: Option[String]): Either[DecodingFailure, Double] = {
        val sku = googlePriceItems.filter { priceItem =>
          (priceItem.regions.contains(region)
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
          case 0  => Left(DecodingFailure(s"No SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude in the following price list: $googlePriceItems", List()))
          case 1 => Right(getPriceFromSku(sku.head))
          case tooMany => Left(DecodingFailure(s"$tooMany SKUs matched with region $region, resourceFamily $resourceFamily, resourceGroup $resourceGroup, $usageType usageType, and description including $descriptionShouldInclude and notIncluding $descriptionShouldNotInclude in the following price list: $googlePriceItems", List()))
        }
      }

      def getPriceFromSku(priceItem: GooglePriceItem): Double = {
        // ToDo: Currently just takes first, make it take either most recent or make it dependent on when the call ran
        priceItem.pricingInfo.head.tieredRates.filter(rate => rate.startUsageAmount.asInt == 0).head.nanos.asInt.toDouble / 1000000000
      }

      def priceList(googlePriceList: GooglePriceList): Either[DecodingFailure, PriceList] = {
        println(s"GOOGLE PRICE LIST: $googlePriceList")
        val filteredByRegion = googlePriceList.priceItems.filter(priceItem => priceItem.regions.contains(region))
        println(s"FILTERED BY REGION: $filteredByRegion" )
        for {
          ssdCostPerGbPerMonth <- getPrice(filteredByRegion, ResourceFamily("Storage"), ResourceGroup("SSD"), UsageType("OnDemand"), None, Some("Regional"))
          hddCostPerGbPerMonth <- getPrice(filteredByRegion, ResourceFamily("Storage"), ResourceGroup("PDStandard"), UsageType("OnDemand"), None, Some("Regional"))
          cpuOnDemandCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("CPU"), UsageType("OnDemand"), None, None)
          ramOnDemandCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("OnDemand"), None, Some("Custom Extended"))
          extendedRamOnDemandCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("OnDemand"), Some("Custom Extended"), None)
          cpuPreemptibleCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("CPU"), UsageType("Preemptible"), None, Some("Custom Extended"))
          ramPreemptibleCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("Preemptible"), None, Some("Custom Extended"))
          extendedRamPreemptibleCostGibibytesPerHour <- getPrice(filteredByRegion, ResourceFamily("Compute"), ResourceGroup("RAM"), UsageType("Preemptible"), Some("Custom Extended"), None)
        } yield {
          val ssdCostPerGbPerHour = ssdCostPerGbPerMonth / (24 * 365 / 12)
          val hddCostPerGbPerHour = hddCostPerGbPerMonth / (24 * 365 / 12)
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
      }

      for {
        googlePriceList <- googlePriceListDecoder.apply(cursor)
        result <- priceList(googlePriceList)
      } yield {
        result
      }
  }

  implicit val googlePriceListDecoder: Decoder[GooglePriceList] = Decoder.forProduct1("skus")(GooglePriceList.apply)
}