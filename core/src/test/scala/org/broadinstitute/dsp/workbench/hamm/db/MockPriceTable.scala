package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.model.Region
import scalikejdbc.DBSession

import scala.collection.mutable

class MockPriceTable extends PriceTableQueries {


  val prices: mutable.Set[PriceRecord] = mutable.Set()

  def insertPriceQuery(priceRecord: PriceRecord)(implicit session: DBSession) ={
    prices += priceRecord
    prices.size
  }

  def getPriceRecordQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord] = {
    prices.find(price => price.name.equals(priceUniqueKey.name) &&
                        price.effectiveDate.equals(priceUniqueKey.effectiveDate))
  }

  def getPriceQuery(priceUniqueKey: PriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double] = {
    getPriceRecordQuery(priceUniqueKey).flatMap(price =>  price.priceItem.hcursor.downField(region.asString).as[Double].toOption)
  }

}
