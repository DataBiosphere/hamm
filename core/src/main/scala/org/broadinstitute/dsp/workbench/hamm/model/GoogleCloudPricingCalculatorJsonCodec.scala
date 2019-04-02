package org.broadinstitute.dsp.workbench.hamm.model

import java.text.SimpleDateFormat

import io.circe.{Decoder, Json}
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import org.broadinstitute.dsp.workbench.hamm.db.PricingCalculatorPriceRecord

object GoogleCloudPricingCalculatorJsonCodec extends HammLogger {


  implicit val priceRecordDecoder: Decoder[List[PricingCalculatorPriceRecord]] = Decoder.instance {
    cursor =>
      val formatter = new SimpleDateFormat("d-MMMM-yyyy")
      for {
        updated <- cursor.downField("updated").as[String]
        priceList <- cursor.downField("gcp_price_list").as[Map[String, Json]]
      } yield {
        priceList.collect {
          case (key, js) if key.contains("COMPUTEENGINE") => {
            val priceType = PriceType.getTypeFromPriceListKey(key, js)
            new PricingCalculatorPriceRecord(key, formatter.parse(updated).toInstant, js)
          }
        }.toList
      }
  }
}
