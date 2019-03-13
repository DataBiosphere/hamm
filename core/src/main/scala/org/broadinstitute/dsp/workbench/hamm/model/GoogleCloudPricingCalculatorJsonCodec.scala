package org.broadinstitute.dsp.workbench.hamm.model

import java.text.SimpleDateFormat
import java.time.Instant

import io.circe.{Decoder, DecodingFailure, Json, KeyDecoder}
import org.broadinstitute.dsde.workbench.hamm.HammLogger

object GoogleCloudPricingCalculatorJsonCodec extends HammLogger {

  implicit val priceDecoder: Decoder[Price] = Decoder.decodeDouble.map(Price(_))
  implicit val tierDecoder: Decoder[Tier] = Decoder.decodeString.map(Tier(_))
  implicit val tierPriceMapDecoder: Decoder[Map[Tier,Price]] = Decoder.decodeMap[String, Double].map(_.map(tieredPrices => Tier(tieredPrices._1) -> Price(tieredPrices._2) ))


  implicit val computeEngineOSPriceItemDecoder: Decoder[ComputeEngineOSPriceItem] = Decoder.instance {
    cursor =>
      if ( cursor.value.isObject) {
        for {
          low <- cursor.downField("low").as[Option[Price]]
          high <- cursor.downField("high").as[Option[Price]]
          highest <- cursor.downField("highest").as[Option[Price]]
          cores <- cursor.downField("cores").as[Option[String]]
          percore <- cursor.downField("percore").as[Option[Boolean]]
        } yield ComputeEngineOSPriceItem(low, high, highest, cores, percore)
      } else Left(DecodingFailure("Not a compute engine OS price item.", List()))
  }

  implicit val computeEngineOSItemListDecoder: Decoder[Map[String, ComputeEngineOSPriceItem]] = Decoder.decodeMap[String, Either[ComputeEngineOSPriceItem, Json]](KeyDecoder[String], Decoder[ComputeEngineOSPriceItem].either(Decoder[Json])).map {
    x => x.collect { case (key, Left(item)) =>  (key, item) }
  }


  implicit val computeEngineOSPriceListDecoder: Decoder[ComputeEngineOSPriceList] = Decoder.instance {
    cursor =>
      for {
        windowsServerCore <- cursor.downField("windows-server-core").as[Option[String]]
        prices <- cursor.as[Map[String, ComputeEngineOSPriceItem]]

      } yield ComputeEngineOSPriceList(windowsServerCore, prices)
  }


  implicit val regionalPriceItemDecoder: Decoder[RegionalPriceItem] = Decoder.instance {
    cursor =>
      if ( cursor.value.isObject && cursor.keys.getOrElse(throw HammException(404, "no keys!")).exists{ Region.stringToRegion.keys.toList.contains(_) }) {
        for {
          cores <- cursor.downField("cores").as[Option[String]]
          memory <- cursor.downField("memory").as[Option[String]]
          maxNumberOfPd <- cursor.downField("maxNumberOfPd").as[Option[Int]]
          maxPdSize <- cursor.downField("maxPdSize").as[Option[Int]]
          ssdRaw <- cursor.downField("ssd").as[Option[Array[Int]]]
          fixed <- cursor.downField("fixed").as[Option[Boolean]]
          freeQuotaQuantity <- cursor.downField("freequota").downField("quantity").as[Option[Double]]
          schedule <- cursor.downField("schedule").as[Option[Map[String, Double]]]
        } yield {
          val gceu = cursor.downField("gceu").focus.map( stringOrIntJson => stringOrIntJson.toString )
          val ssd = ssdRaw.map(rawArray => rawArray.max)
          val prices: Map[Region, Option[Price]] = Region.stringToRegion.keys.map(key => Region.stringToRegion(key) -> cursor.downField(key).as[Option[Price]].getOrElse(None)).toMap
          RegionalPriceItem(prices, cores, memory, gceu, maxNumberOfPd, maxPdSize, ssd, fixed, freeQuotaQuantity, schedule)
        }
      } else Left(DecodingFailure("Not a regional price item", List()))
  }

  implicit val regionalPriceMapListDecoder: Decoder[Map[String, RegionalPriceItem]] = Decoder.decodeMap[String, Either[RegionalPriceItem, Json]](KeyDecoder[String], Decoder[RegionalPriceItem].either(Decoder[Json])).map {
    x => x.collect { case (key, Left(item)) =>  (key, item) }
  }


  implicit val tieredPriceItemDecoder: Decoder[TieredPriceItem] = Decoder.instance {
    cursor =>
      if (cursor.value.isObject && cursor.downField("tiers").succeeded) {
        for {
          prices <- cursor.downField("tiers").as[Map[Tier, Price]]
        } yield TieredPriceItem(prices)
      } else Left(DecodingFailure("Not a regional price item", List()))
  }


  implicit val tieredPriceMapListDecoder: Decoder[Map[String, TieredPriceItem]] = Decoder.decodeMap[String, Either[TieredPriceItem, Json]](KeyDecoder[String], Decoder[TieredPriceItem].either(Decoder[Json])).map {
    x => x.collect { case (key, Left(item)) =>  (key, item) }
  }

  implicit val PriceListsDecoder: Decoder[PriceLists] = Decoder.instance {
    cursor =>
      for {
        regionalPriceItems <- cursor.as[Map[String, RegionalPriceItem]]
        tieredPriceItems <- cursor.as[Map[String, TieredPriceItem]]
        sustainedUseBase  <- cursor.downField("sustained_use_base").as[Double]
        sustainedUseTiered  <- cursor.downField("sustained_use_tiers").as[Map[Tier, Price]]
        computeEngineOSPriceList: ComputeEngineOSPriceList <- cursor.downField("CP-COMPUTEENGINE-OS").as[ComputeEngineOSPriceList]
      } yield PriceLists(regionalPriceItems, tieredPriceItems, computeEngineOSPriceList, Price(sustainedUseBase))
  }

  implicit val googleCloudPricingDecoder: Decoder[GoogleCloudPricing] = Decoder.instance {
    cursor =>
      val formatter = new SimpleDateFormat("d-MMMM-yyyy")
      for {
        version <- cursor.downField("version").as[String]
        updated <- cursor.downField("updated").as[String]
        priceList <- cursor.downField("gcp_price_list").as[PriceLists]
      } yield GoogleCloudPricing(version, formatter.parse(updated).toInstant, priceList)
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
