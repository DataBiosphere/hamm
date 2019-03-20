package org.broadinstitute.dsp.workbench.hamm.db

import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class PriceTableSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  it should "insert and get a price" in { implicit session =>
    priceTable.getPriceQuery(TestData.testPriceUniqueKey) shouldBe None

    priceTable.insertPriceQuery(TestData.testPriceRecord)

    priceTable.getPriceQuery(TestData.testPriceUniqueKey) shouldBe Some(TestData.testPriceRecord)
  }




}