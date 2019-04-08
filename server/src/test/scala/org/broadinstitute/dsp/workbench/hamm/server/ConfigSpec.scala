package org.broadinstitute.dsp.workbench.hamm.server

import org.broadinstitute.dsp.workbench.hamm.dao.GoogleConfig
import org.broadinstitute.dsp.workbench.hamm.db.LiquibaseConfig
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamConfig
import org.http4s.Uri
import org.scalatest.{FlatSpec, Matchers}

class ConfigSpec extends FlatSpec with Matchers{
  "Config" should "read configuration correctly" in {
    val config = Config.appConfig
    val expectedConfig = AppConfig(
      GoogleConfig(
        Uri.unsafeFromString("https://cloudbilling.googleapis.com"),
        Uri.unsafeFromString("https://cloudpricingcalculator.appspot.com/static/data/pricelist.json"),
        "",
        ""
      ),
      LiquibaseConfig("org/broadinstitute/dsp/workbench/hamm/liquibase/changelog.xml", true),
      SamConfig(Uri.unsafeFromString("https://sam.dsde-dev.broadinstitute.org:443"))
    )
    config shouldBe Right(expectedConfig)
  }
}
