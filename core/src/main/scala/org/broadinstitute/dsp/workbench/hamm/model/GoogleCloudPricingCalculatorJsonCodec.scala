package org.broadinstitute.dsp.workbench.hamm.model

import java.text.SimpleDateFormat
import java.time.Instant

import io.circe.{Decoder, Json}
import org.broadinstitute.dsde.workbench.hamm.HammLogger
import org.broadinstitute.dsde.workbench.hamm.db.PriceRecord

object GoogleCloudPricingCalculatorJsonCodec extends HammLogger {


  implicit val priceRecordDecoder: Decoder[List[PriceRecord]] = Decoder.instance {
    cursor =>
      val formatter = new SimpleDateFormat("d-MMMM-yyyy")
      for {
        updated <- cursor.downField("updated").as[String]
        priceList <- cursor.downField("gcp_price_list").as[Map[String, Json]]
      } yield {
        priceList.collect {
          case (key, js) if key.contains("COMPUTEENGINE") => {
            val priceType = PriceType.getTypeFromPriceListKey(key, js)
            PriceRecord(key, formatter.parse(updated).toInstant, priceType, js)
          }
        }.toList
      }
  }
}

case class RegionalPriceItem(prices: Map[Region, Option[Price]],
                             cores: Option[String],
                             memory: Option[String],
                             gceu: Option[String],
                             maxNumberOfPd: Option[Int],
                             maxPdSize: Option[Int],
                             ssd: Option[Int],
                             fixed: Option[Boolean],
                             freeQuotaQuantity: Option[Double],
                             schedule: Option[Map[String, Double]])

case class Price(asDouble: Double)

case class TieredPriceItem(prices: Map[Tier, Price])

case class Tier(asString: String)

case class ComputeEngineOSPriceList(windowsServerCore: Option[String],
                                    prices: Map[String, ComputeEngineOSPriceItem])

case class ComputeEngineOSPriceItem(low: Option[Price],
                                    high: Option[Price],
                                    highest: Option[Price],
                                    cores: Option[String],
                                    percore: Option[Boolean])


case class GoogleCloudPricing(version: String,
                              updated: Instant,
                              priceList: PriceLists)

case class PriceLists(regionalPriceList: Map[String, RegionalPriceItem],
                      tieredPriceList: Map[String, TieredPriceItem],
                      computeEngineOSPriceList: ComputeEngineOSPriceList,
                      sustainedUseBase: Price)
