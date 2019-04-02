package org.broadinstitute.dsp.workbench.hamm.config

import net.ceedubs.ficus.readers.ValueReader
import org.http4s.Uri

package object config {

  implicit val GoogleConfigReader: ValueReader[GoogleConfig] = ValueReader.relative { config =>
    GoogleConfig(Uri.unsafeFromString(config.getString("googleDefaultPricingUrl")))
  }


  implicit val LiquibaseConfigReader: ValueReader[LiquibaseConfig] = ValueReader.relative { config =>
    LiquibaseConfig(
      config.getString("changelog"),
      config.getBoolean("initWithLiquibase")
    )
  }


  implicit val SamConfigReader: ValueReader[SamConfig] = ValueReader.relative { config =>
    SamConfig(
      Uri.unsafeFromString(config.getString("samUrl"))
    )
  }


}
