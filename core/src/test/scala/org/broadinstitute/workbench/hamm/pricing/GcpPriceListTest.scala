package org.broadinstitute.workbench.hamm.pricing


import io.circe.parser._
import JsonCodec._
import org.broadinstitute.workbench.hamm._


object GcpPricingTest extends HammTestSuite {
  test("SKUsDecoder should be able to decode PriceList"){
    val region = Region.stringToRegion("us-west2")
    val machineType = MachineType.Custom
    val res = for {
      json <- parse(TestData.sampleGooglePriceJson)
      googlePriceList <- json.as[GooglePriceList]
      r <- GcpPricing.parsePriceList(googlePriceList, List(ComputePriceKey(region, machineType, UsageType.Preemptible)), List(StoragePriceKey(region, DiskType.SSD)))
    } yield {
      val expectedResponse = PriceList(
        ComputePriceList(Map(ComputePriceKey(Region.USwest2,MachineType.Custom,UsageType.Preemptible) -> ComputePrices(0.007986,0.001076))),
        StoragePriceList(Map(StoragePriceKey(Region.USwest2,DiskType.SSD) -> .0002794520547945205)))
      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }
}

