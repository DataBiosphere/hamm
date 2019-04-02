package org.broadinstitute.dsp.workbench.hamm.model

import java.time.Instant

import io.circe.{Decoder, Json}

object GooglePriceListJsonCodec {

  implicit val categoryDecoder: Decoder[Category] = Decoder.instance { cursor =>
    for {
      resourceFamily <- cursor.downField("resourceFamily").as[ResourceFamily]
      resourceGroup <- cursor.downField("resourceGroup").as[String]
      usageType <- cursor.downField("usageType").as[UsageType]
    } yield Category(resourceFamily, ResourceGroup(resourceGroup), usageType)
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
      effectiveTime <- cursor.downField("pricingExpression").downField("effectiveTime").as[Instant]
      usageUnit <- cursor.downField("pricingExpression").downField("usageUnit").as[String]
      baseUnit <- cursor.downField("pricingExpression").downField("baseUnit").as[String]
      tieredRates <- cursor.downField("pricingExpression").downField("tieredRates").as[List[TieredRate]]
    } yield PricingInfo(effectiveTime, UsageUnit(usageUnit), BaseUnit(baseUnit), tieredRates)
  }

  implicit val googlePriceItemDecoder: Decoder[GooglePriceItem] = Decoder.instance {
    cursor =>
      for {
        category <- cursor.downField("category").as[Category]
        name <- cursor.downField("name").as[String]
        skuId <- cursor.downField("skuId").as[String]
        description <- cursor.downField("description").as[String]
        regions <- cursor.downField("serviceRegions").as[List[Region]]
        pricingInfo <- cursor.downField("pricingInfo").as[List[PricingInfo]]
      } yield GooglePriceItem(SkuName(name), SkuDescription(description), category, regions, pricingInfo)
  }

  implicit val resourceFamilyDecoder: Decoder[ResourceFamily] = Decoder.decodeString.emap{
    resourceFamily => ResourceFamily.stringToResourceFamily.get(resourceFamily).toRight(s"Irrelavent item with resource family $resourceFamily")
  }

  implicit val usageTypeDecoder: Decoder[UsageType] = Decoder.decodeString.emap{
    usageType => UsageType.stringToUsageType.get(usageType).toRight(s"Irrelavent item with usage type $usageType")
  }

  implicit val regionDecoder: Decoder[Region] = Decoder.decodeString.emap{
    region => Region.stringToRegion.get(region).toRight(s"Irrelavent item with region $region")
  }

  implicit val listGooglePriceItemDecoder: Decoder[List[GooglePriceItem]] = Decoder.decodeList(Decoder[GooglePriceItem].either(Decoder[Json])).map {
    x =>
      x.flatMap{
        item =>
          item.left.toOption
      }
  }

  implicit val googlePriceListDecoder: Decoder[GooglePriceList] = Decoder.instance { cursor =>
    for {
      skus <- cursor.downField("skus").as[List[GooglePriceItem]]
    } yield GooglePriceList(skus)
  }
}