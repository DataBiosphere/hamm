package org.broadinstitute.dsp.workbench.hamm.db

import org.broadinstitute.dsp.workbench.hamm.{TestComponent, TestData}
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scalikejdbc.scalatest.AutoRollback

class PricingCalculatorPriceTableSpec extends FlatSpec with Matchers with AutoRollback with TestComponent {

  it should "insert and get a price" in { implicit session =>
    println("startt")
    pricingCalculatorPriceTable.getPriceQuery(TestData.testPriceUniqueKey, TestData.testRegion) shouldBe None

    println("we're here")
    pricingCalculatorPriceTable.insertPriceQuery(TestData.testPriceRecord)

    println("now we're here")
    pricingCalculatorPriceTable.getPriceQuery(TestData.testPriceUniqueKey2, TestData.testRegion) shouldBe Some(TestData.testPriceRecord)
    println("endd")
  }




}