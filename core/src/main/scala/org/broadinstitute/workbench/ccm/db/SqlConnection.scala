package org.broadinstitute.workbench.ccm.db

import cats.effect.{Async, ContextShift, IO}
import ciris.{envF, loadConfig}
import doobie.util.transactor.Transactor
import ciris.cats.effect._
import cats.implicits._

class SqlConnection[F[_]: ContextShift: Async](config: SqlConfig) {
  val dbTransactor: Transactor[F] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver", // driver classname
    s"jdbc:postgresql://127.0.0.1:5432/ccm", // connect URL (driver-specific)
    config.user.asString,              // user
    config.password.asString                       // password
  )
}

object SqlConnection {
  def apply[F[_]: ContextShift: Async](config: SqlConfig): SqlConnection[F] = new SqlConnection[F](config)

  val readCondigFromEnv: IO[SqlConfig] = loadConfig(
    envF[IO, String]("DB_USER"),
    envF[IO, String]("DB_PASSWORD")
  ){ (username, password) =>
    SqlConfig(DbUser(username), DbPassword(password))
  }.flatMap(x => IO.fromEither(x.leftMap(_.toException).leftWiden))
}

final case class DbUser(asString: String) extends AnyVal
final case class DbPassword(asString: String) extends AnyVal
final case class SqlConfig(user: DbUser, password: DbPassword)