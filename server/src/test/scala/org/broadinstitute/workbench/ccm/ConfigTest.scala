package org.broadinstitute.workbench.ccm

import org.http4s.Uri
import Config._

object ConfigTest extends CcmTestSuite {
  test("Config should load configuration file properly"){
    val config = Config.appConfig
    val expectedConfig = AppConfig(Grpc(9999), Uri.unsafeFromString("https://cloudpricingcalculator.appspot.com/static/data/pricelist.json"))
    assertEquals(config, Right(expectedConfig))
  }
}