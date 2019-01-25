package org.broadinstitute.workbench.hamm
package db

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor

object DummyDbTransactor {
  val dbConfig = SqlConfig(
    DbUser("ccm"),
    DbPassword("123"),
    5433
  )

  def transactor(config: SqlConfig = dbConfig)(implicit cs: ContextShift[IO]): Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",                        // driver classname
    s"jdbc:postgresql://127.0.0.1:${config.port}/ccm",   // connect URL
    config.user.asString,                                   // username
    config.password.asString                                     // password
  )
}