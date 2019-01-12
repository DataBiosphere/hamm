package org.broadinstitute.workbench.ccm
package db

object DBConfig {
  val dbConfig = SqlConfig(
    DbUrl("jdbc:postgresql://127.0.0.1:5432/ccm"),
    DbUser("ccm"),
    DbPassword("123456")
  )
}
