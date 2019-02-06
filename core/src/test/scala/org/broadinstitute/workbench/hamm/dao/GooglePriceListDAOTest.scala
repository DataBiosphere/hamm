package org.broadinstitute.workbench.hamm.dao

import io.circe.parser._
import org.broadinstitute.workbench.hamm._
import org.broadinstitute.workbench.hamm.dao.GooglePriceListDAO
import org.broadinstitute.workbench.hamm.model.GooglePriceListJsonCodec._
import org.broadinstitute.workbench.hamm.model._


object GooglePriceListDAOTest extends HammTestSuite {
  test("SKUsDecoder should be able to decode PriceList"){
    val region = Region.stringToRegion("us-west2")
    val machineType = MachineType.Custom
    val res = for {
      json <- parse(TestData.sampleGooglePriceJson)
      googlePriceList <- json.as[GooglePriceList]
      r <- GooglePriceListDAO.parsePriceList(googlePriceList, List(ComputePriceKey(region, machineType, UsageType.Preemptible)), List(StoragePriceKey(region, DiskType.SSD)))
    } yield {
      val expectedResponse = PriceList(
        ComputePriceList(Map(ComputePriceKey(Region.USwest2,MachineType.Custom,UsageType.Preemptible) -> ComputePrices(0.007986,0.001076))),
        StoragePriceList(Map(StoragePriceKey(Region.USwest2,DiskType.SSD) -> .0002794520547945205)))
      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }
}

