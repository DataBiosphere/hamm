package org.broadinstitute.dsp.workbench.hamm.db

import com.typesafe.config.ConfigFactory

// initialize database tables and connection pool only once
object DbSingleton {
  import org.broadinstitute.dsp.workbench.hamm.TestExecutionContext.testExecutionContext

  val config = ConfigFactory.load()
  val ref: DbReference = DbReference.init(ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load()))
}
