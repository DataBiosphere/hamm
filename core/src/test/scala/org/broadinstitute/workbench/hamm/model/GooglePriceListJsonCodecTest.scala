package org.broadinstitute.workbench.hamm.model

import io.circe.parser._
import org.broadinstitute.workbench.hamm._
//import org.broadinstitute.workbench.hamm.dao.GooglePriceListDAO
import org.broadinstitute.workbench.hamm.model.GooglePriceListJsonCodec._

object GooglePriceListJsonCodecTest extends HammTestSuite {

  test("SKUsDecoder should be able to decode SKUs"){
    val res = for {
      json <- parse(TestData.sampleGooglePriceJson)
      r <- json.as[GooglePriceList]
    } yield {
      val expectedResponse = GooglePriceList(
        List(
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0000-BBAF-9069"),
            SkuId("0000-BBAF-9069"),
            SkuDescription("Preemptible Custom Instance Core running globally"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("CPU"),
              UsageType.Preemptible),
            List(Region.Global),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(6986000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/9420-2C0D-17F3"),
            SkuId("9420-2C0D-17F3"),
            SkuDescription("Preemptible Custom Ram running globally"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Storage,
              ResourceGroup("SSD"),
              UsageType.Preemptible),
            List(Region.Global),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(20931000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/472A-2C0D-17F3"),
            SkuId("472A-2C0D-17F3"),
            SkuDescription("Custom Extended Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("RAM"),
              UsageType.OnDemand),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(10931000)))))),

          //i think this one might be wrong
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/5662-928E-19C3"),
            SkuId("5662-928E-19C3"),
            SkuDescription("Preemptible Custom Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("RAM"),
              UsageType.Preemptible),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(1076000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/4EAF-BBAF-9069"),
            SkuId("4EAF-BBAF-9069"),
            SkuDescription("Preemptible Custom Instance Core running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("CPU"),
              UsageType.Preemptible),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(7986000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/5662-928E-19C3"),
            SkuId("5662-928E-19C3"),
            SkuDescription("Custom Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("RAM"),
              UsageType.OnDemand),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(1076000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/2037-B859-1728"),
            SkuId("2037-B859-1728"),
            SkuDescription("Custom Instance Core running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("CPU"),
              UsageType.OnDemand),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(37970000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/878F-E2CC-F899"),
            SkuId("878F-E2CC-F899"),
            SkuDescription("Storage PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Storage,
              ResourceGroup("PDStandard"),
              UsageType.OnDemand),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(96000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/001D-204A-23DA"),
            SkuId("001D-204A-23DA"),
            SkuDescription("Commitment v1: Cpu in Montreal for 1 Year"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("CPU"),
              UsageType.Commit1Yr),
            List(Region.Northamericanortheast1),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(21925000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0589-AA00-68BD"),
            SkuId("0589-AA00-68BD"),
            SkuDescription("SSD backed PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Storage,
              ResourceGroup("SSD"),
              UsageType.OnDemand),
            List(Region.USwest2),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(204000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/077F-E880-3C8E"),
            SkuId("077F-E880-3C8E"),
            SkuDescription("Preemptible Custom Extended Instance Core running in Sydney"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily.Compute,
              ResourceGroup("CPU"),
              UsageType.Preemptible),
            List(Region.Australiasoutheast1),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(8980000))))))))

      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }

//  test("SKUsDecoder should be able to decode PriceList"){
//    val region = Region.stringToRegion("us-west2")
//    val machineType = MachineType.Custom
//    val res = for {
//      json <- parse(TestData.sampleGooglePriceJson)
//      googlePriceList <- json.as[GooglePriceList]
//      r <- GooglePriceListDAO.parsePriceList(googlePriceList, List(ComputePriceKey(region, machineType, UsageType.Preemptible)), List(StoragePriceKey(region, DiskType.SSD)))
//    } yield {
//      val expectedResponse = PriceList(
//        ComputePriceList(Map(ComputePriceKey(Region.USwest2,MachineType.Custom,UsageType.Preemptible) -> ComputePrices(0.007986,0.001076))),
//        StoragePriceList(Map(StoragePriceKey(Region.USwest2,DiskType.SSD) -> .0002794520547945205)))
//      assertEquals(r, expectedResponse)
//    }
//    res.fold[Unit](e => throw e, identity)
//  }
}
