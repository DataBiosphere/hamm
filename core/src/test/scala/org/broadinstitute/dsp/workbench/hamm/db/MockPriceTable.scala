package org.broadinstitute.dsp.workbench.hamm.db

import scalikejdbc.DBSession

import scala.collection.mutable

class MockPriceTable extends PriceTableQueries {


  val prices: mutable.Set[PriceRecord] = mutable.Set()

  def insertPriceQuery(priceRecord: PriceRecord)(implicit session: DBSession) ={
    prices += priceRecord
    prices.size
  }

  def getPriceQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord] = {
    prices.find(price => price.name.equals(priceUniqueKey.name) &&
                        price.effectiveDate.equals(priceUniqueKey.effectiveDate))
  }

}
