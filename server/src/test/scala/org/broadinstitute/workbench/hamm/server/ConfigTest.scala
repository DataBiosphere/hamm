//package org.broadinstitute.workbench.hamm.server
//
//import org.broadinstitute.workbench.hamm.HammTestSuite
//import org.broadinstitute.workbench.hamm.service.{AppConfig, Config, Grpc}
//import org.http4s.Uri
//
//object ConfigTest extends HammTestSuite {
//  test("Config should load configuration file properly"){
//    val config = Config.appConfig
//    val expectedConfig = AppConfig(Grpc(9999), Uri.unsafeFromString("https://cloudpricingcalculator.appspot.com/static/data/pricelist.json"))
//    assertEquals(config, Right(expectedConfig))
//  }
//}
