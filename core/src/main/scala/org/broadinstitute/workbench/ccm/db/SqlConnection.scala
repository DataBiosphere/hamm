package org.broadinstitute.workbench.ccm.db

import cats.effect.{Async, ContextShift}
import doobie.util.transactor.Transactor

class SqlConnection[F[_]: ContextShift: Async](config: SqlConfig) {
  val dbTransactor: Transactor[F] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver", // driver classname
    config.url.asString, // connect URL (driver-specific)
    config.user.asString,              // user
    config.password.asString                       // password
  )
}

object SqlConnection {
  def apply[F[_]: ContextShift: Async](config: SqlConfig): SqlConnection[F] = new SqlConnection[F](config)
}

final case class DbUrl(asString: String) extends AnyVal
final case class DbUser(asString: String) extends AnyVal
final case class DbPassword(asString: String) extends AnyVal
final case class SqlConfig(url: DbUrl, user: DbUser, password: DbPassword)