package org.broadinstitute.workbench.hamm
package pricing

import io.circe.Decoder

//TODO: this needs to be updated to use https://cloud.google.com/billing/v1/how-tos/catalog-api
object JsonCodec {
  implicit val cpuDecoder: Decoder[CpuCost] = Decoder.forProduct1("us")(CpuCost.apply)
  implicit val ramDecoder: Decoder[RamCost] = Decoder.forProduct1("us")(RamCost.apply)
  implicit val computeCostDecoder: Decoder[ComputeCost] = Decoder.forProduct2("CP-DB-PG-CUSTOM-VM-CORE", "CP-DB-PG-CUSTOM-VM-RAM")(ComputeCost.apply)
  implicit val gcpPriceListDecoder: Decoder[GcpPriceList] = Decoder.forProduct1("gcp_price_list")(GcpPriceList.apply)


 // implicit val categoryDecoder: Decoder[Category] = Decoder.forProduct4("serviceDisplayName", "resourceFamily", "resourceGroup", "usageType")(Category.apply)
  implicit val categoryDecoder: Decoder[Category] = Decoder.instance{ cursor =>
    for {
      serviceDisplayName <- cursor.downField("serviceDisplayName").as[String]
      resourceFamily <- cursor.downField("resourceFamily").as[String]
      resourceGroup <- cursor.downField("resourceGroup").as[String]
      usageType <- cursor.downField("usageType").as[String]
    } yield Category(ServiceDisplayName(serviceDisplayName), ResourceFamily(resourceFamily), ResourceGroup(resourceGroup), UsageType(usageType))
  }

  implicit val TieredRateDecoder: Decoder[TieredRate] = Decoder.instance { cursor =>
    for {
      startUsageAmount <- cursor.downField("startUsageAmount").as[Int]
      currencyCode <- cursor.downField("unitPrice").downField("currencyCode").as[String]
      units <- cursor.downField("unitPrice").downField("units").as[Int]
      nanos <- cursor.downField("unitPrice").downField("nanos").as[Int]
    } yield TieredRate(StartUsageAmount(startUsageAmount), CurrencyCode(currencyCode), Units(units), Nanos(nanos))
  }

  implicit val pricingInfoDecoder: Decoder[PricingInfo] = Decoder.instance{ cursor =>
    for {
      usageUnit <- cursor.downField("pricingExpression").downField("usageUnit").as[String]
      tieredRates <- cursor.downField("pricingExpression").downField("tieredRates").as[List[TieredRate]]
    } yield PricingInfo(UsageUnit(usageUnit), tieredRates)
  }

  implicit val googlePriceItemDecoder: Decoder[GooglePriceItem] = Decoder.instance{
    cursor =>
      for {
        name <- cursor.downField("name").as[String]
        skuId <- cursor.downField("skuId").as[String]
        description <- cursor.downField("description").as[String]
        category <- cursor.downField("category").as[Category]
        serviceRegions <- cursor.downField("serviceRegions").as[List[String]]
        pricingInfo <- cursor.downField("pricingInfo").as[List[PricingInfo]]
      } yield GooglePriceItem(SkuName(name), SkuId(skuId), SkuDescription(description), category, serviceRegions.map(ServiceRegion(_)), pricingInfo)
  }

  implicit val skusDecoder: Decoder[Skus] = Decoder.forProduct1("skus")(Skus.apply)
}