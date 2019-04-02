package org.broadinstitute.dsp.workbench.hamm.dao

import cats.effect.Sync
import org.broadinstitute.dsp.workbench.hamm.config.GoogleConfig
import org.broadinstitute.dsp.workbench.hamm.model.GooglePriceList
import org.broadinstitute.dsp.workbench.hamm.model.GooglePriceListJsonCodec.googlePriceListDecoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client


class GoogleSKUPriceListDAO[F[_]: Sync](httpClient: Client[F], config: GoogleConfig) {

  def getPriceRecords(): F[GooglePriceList] = {
    httpClient.expect[GooglePriceList](config.googleDefaultPricingUrl)
  }

}