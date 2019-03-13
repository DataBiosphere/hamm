package org.broadinstitute.dsde.workbench.hamm.dao

import org.broadinstitute.dsde.workbench.hamm.{HammLogger, TestData}
import org.broadinstitute.dsde.workbench.hamm.model.GoogleCloudPricing
import org.broadinstitute.dsde.workbench.hamm.model.GoogleCloudPricingCalculatorJsonCodec._
import org.scalatest.{FlatSpec, Matchers}
import io.circe.parser._

// ToDo: Replace this. Not bothering to add tests here because we're going to be using a different way of getting the price objects
class GooglePriceListDAOSpec extends FlatSpec with Matchers with HammLogger {

  it should "get google's price list" in {
    println("START TEST")
    for {
      //json <- parse(sample)
      //result <- json.hcursor.keys.get
      json <- parse(TestData.sampleList)
      googlePriceList <- json.as[GoogleCloudPricing]
    } yield {
//      println("we're here")
//      val result = json.hcursor.keys.get
//      println("we're here now")
//      println("RESULT " + result.toString)
      ()
      println(googlePriceList.toString)
      googlePriceList
    }
  }

//  test("SKUsDecoder should be able to decode PriceList") {
////    val region = Region.stringToRegion("us-west2")
////    val machineType = MachineType.Custom
//    val res = for {
//      json <- parse(TestData.sampleGooglePriceJson)
//      googlePriceList <- json.as[GooglePriceList]
//      r <- GooglePriceListDAO.parsePriceList(googlePriceList, List(ComputePriceKey(region, machineType, UsageType.Preemptible)), List(StoragePriceKey(region, DiskType.SSD)))
//    } yield {
//      val expectedResponse = PriceList(
//        ComputePriceList(Map(ComputePriceKey(Region.USwest2, MachineType.Custom, UsageType.Preemptible) -> ComputePrices(0.007986, 0.001076))),
//        StoragePriceList(Map(StoragePriceKey(Region.USwest2, DiskType.SSD) -> .0002794520547945205)))
//      assertEquals(r, expectedResponse)
//    }
//    res.fold[Unit](e => throw e, identity)
//  }
}

