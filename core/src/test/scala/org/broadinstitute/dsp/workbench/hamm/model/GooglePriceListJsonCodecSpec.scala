package org.broadinstitute.dsp.workbench.hamm.model

import io.circe.parser.parse
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.broadinstitute.dsp.workbench.hamm.db.PriceRecord
import org.broadinstitute.dsp.workbench.hamm.model.GoogleCloudPricingCalculatorJsonCodec.priceRecordDecoder
import org.scalatest.{FlatSpec, Matchers}

class GooglePriceListJsonCodecSpec extends FlatSpec with Matchers {


  it should "decode google's price list" in {
    println("START TEST")
    for {
      json <- parse(TestData.sampleList)
      googlePriceList <- json.as[List[PriceRecord]]
    } yield {
      println(googlePriceList.toString)
      googlePriceList

    }
  }
}
