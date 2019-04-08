package org.broadinstitute.dsp.workbench.hamm.server

import cats.implicits._
import org.broadinstitute.dsp.workbench.hamm.dao.GoogleConfig
import org.broadinstitute.dsp.workbench.hamm.db.LiquibaseConfig
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamConfig
import pureconfig.generic.auto._
import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.error.ExceptionThrown

object Config {
  implicit val uriConfigReader: ConfigReader[Uri] = ConfigReader.fromString(
    s => Uri.fromString(s).leftMap(err => ExceptionThrown(err))
  )
  val appConfig = pureconfig.loadConfig[AppConfig].leftMap(failures => new RuntimeException(failures.toList.map(_.description).mkString("\n")))
}

final case class AppConfig(google: GoogleConfig, liquibase: LiquibaseConfig, sam: SamConfig)