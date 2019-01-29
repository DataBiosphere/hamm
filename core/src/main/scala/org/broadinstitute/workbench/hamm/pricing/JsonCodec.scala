package org.broadinstitute.workbench.hamm
package pricing

import io.circe.{Decoder, DecodingFailure, Json}

//TODO: this needs to be updated to use https://cloud.google.com/billing/v1/how-tos/catalog-api
object JsonCodec {

  implicit val categoryDecoder: Decoder[Category] = Decoder.instance { cursor =>
    for {
      serviceDisplayName <- cursor.downField("serviceDisplayName").as[String]
      resourceFamily <- cursor.downField("resourceFamily").as[String]
      resourceGroup <- cursor.downField("resourceGroup").as[String]
      usageType <- cursor.downField("usageType").as[String]
    } yield Category(ServiceDisplayName(serviceDisplayName), ResourceFamily.stringToResourceFamily(resourceFamily), ResourceGroup(resourceGroup), UsageType.stringToUsageType(usageType))
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
    cursor =>
      for {
        resourceFamily <- cursor.downField("category").downField("resourceFamily").as[String]
        _ <- if (ResourceFamily.stringToResourceFamily.keys.toList.contains(resourceFamily)) Right(resourceFamily) else Left(DecodingFailure(s"Irrelavent item with resource family $resourceFamily", List()))
        name <- cursor.downField("name").as[String]
        skuId <- cursor.downField("skuId").as[String]
        description <- cursor.downField("description").as[String]
        category <- cursor.downField("category").as[Category]
        regions <- cursor.downField("serviceRegions").as[List[String]]
        pricingInfo <- cursor.downField("pricingInfo").as[List[PricingInfo]]
      } yield GooglePriceItem(SkuName(name), SkuId(skuId), SkuDescription(description), category, regions.map(Region.stringToRegion(_)), pricingInfo)
  }

  implicit val listGooglePriceItemDecoder: Decoder[List[GooglePriceItem]] = Decoder.decodeList(Decoder[GooglePriceItem].either(Decoder[Json])).map {
    x =>
      val thing = x.flatMap{
        item =>
          item.left.toOption
      }
      thing
  }

  implicit val googlePriceListDecoder: Decoder[GooglePriceList] = Decoder.instance { cursor =>
    for {
      skus <- cursor.downField("skus").as[List[GooglePriceItem]]
    } yield GooglePriceList(skus)
  }
}
