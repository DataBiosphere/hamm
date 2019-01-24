package org.broadinstitute.workbench.hamm
package server

import org.http4s.Uri
import org.broadinstitute.workbench.hamm.server.Config._

object ConfigTest extends HammTestSuite {
  test("Config should load configuration file properly"){
    val config = Config.appConfig
    val expectedConfig = AppConfig(Grpc(9999), Uri.unsafeFromString("https://cloudpricingcalculator.appspot.com/static/data/pricelist.json"))
    assertEquals(config, Right(expectedConfig))
  }
}