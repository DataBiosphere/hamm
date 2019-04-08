package org.broadinstitute.dsp.workbench.hamm.db

// initialize database tables and connection pool only once
object DbSingleton {
  import org.broadinstitute.dsp.workbench.hamm.TestExecutionContext.testExecutionContext

  val liquidbaseConfig = LiquibaseConfig("org/broadinstitute/dsp/workbench/hamm/liquibase/changelog.xml", true)
  val ref: DbReference = DbReference.init(liquidbaseConfig)
}
