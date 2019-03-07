package org.broadinstitute.workbench.hamm

import net.ceedubs.ficus.readers.ValueReader
import org.http4s.Uri

package object config {

  implicit val cromwellConfigReader: ValueReader[CromwellConfig] = ValueReader.relative { config =>
    CromwellConfig(
      Uri.unsafeFromString(config.getString("cromwellUrl"))
    )
  }

  implicit val databaseConfigReader: ValueReader[DatabaseConfig] = ValueReader.relative { config =>
    DatabaseConfig(
      Uri.unsafeFromString(config.getString("url")),
      config.getString("username"),
      config.getString("password")
    )
  }

  implicit val GoogleConfigReader: ValueReader[GoogleConfig] = ValueReader.relative { config =>
    GoogleConfig(
      Uri.unsafeFromString(config.getString("googleCloudBillingUrl")),
      Uri.unsafeFromString(config.getString("googleDefaultPricingUrl")),
      config.getString("serviceId"),
      config.getString("serviceKey")
    )
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
