package org.broadinstitute.workbench.ccm.db

import cats.effect.{Async, ContextShift, IO, _}
import cats.implicits._
import ciris.cats.effect._
import ciris.{envF, loadConfig}
import doobie._
import doobie.hikari._

object DbTransactorResource {
  def transactor[F[_]: Async: ContextShift](config: SqlConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[F]    // our transaction EC
      xa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",                        // driver classname
        s"jdbc:postgresql://127.0.0.1:5432/ccm",   // connect URL
        config.user.asString,                                   // username
        config.password.asString,                                     // password
        ce,                                     // await connection here
        te                                      // execute JDBC operations here
      )
    } yield xa

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