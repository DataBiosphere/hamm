package org.broadinstitute.dsp.workbench.hamm.dao

import cats.effect.Sync
import org.broadinstitute.dsp.workbench.hamm.config.GoogleConfig
import org.broadinstitute.dsp.workbench.hamm.db.PricingCalculatorPriceRecord
import org.broadinstitute.dsp.workbench.hamm.model.GoogleCloudPricingCalculatorJsonCodec.priceRecordDecoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client


class GooglePriceCalculatorDAO[F[_]: Sync](httpClient: Client[F], config: GoogleConfig) {

  def getPriceRecords(): F[List[PricingCalculatorPriceRecord]] = {
    httpClient.expect[List[PricingCalculatorPriceRecord]](config.googleDefaultPricingUrl)
  }

}