package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.model.Region
import scalikejdbc.DBSession

import scala.collection.mutable

class MockPricingCalculatorPriceTable extends PricingCalculatorPriceTableQueries {


  val prices: mutable.Set[PricingCalculatorPriceRecord] = mutable.Set()

  def insertPriceQuery(priceRecord: PricingCalculatorPriceRecord)(implicit session: DBSession) ={
    prices += priceRecord
    prices.size
  }

  def getPriceRecordQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey)(implicit session: DBSession): Option[PricingCalculatorPriceRecord] = {
    prices.find(price => price.name.equals(priceUniqueKey.name) &&
                        price.effectiveDate.equals(priceUniqueKey.effectiveDate))
  }

  def getPriceQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double] = {
    getPriceRecordQuery(priceUniqueKey).flatMap(price =>  price.priceItem.hcursor.downField(region.asString).as[Double].toOption)
  }

}
