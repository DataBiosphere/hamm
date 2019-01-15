import sbt._

object Dependencies {
  val minitestVersion = "2.2.2"
  val circeVersion = "0.10.0"
  val http4sVersion = "0.20.0-M4"
  val grpcNettyVersion = "1.17.1"
  val cirisVersion = "0.12.1"
  val doobieVersion = "0.7.0-M1"

  val common = List(
    "io.grpc" % "grpc-netty" % grpcNettyVersion,
    "io.monix" %% "minitest" % minitestVersion % "test",
    "io.monix" %% "minitest-laws" % minitestVersion % "test",
    "io.chrisdavenport" %% "log4cats-slf4j"   % "0.2.0",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion % "test",
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "com.github.pureconfig" %% "pureconfig" % "0.10.1",
    "io.sentry" % "sentry-logback" % "1.7.16", // see doc https://docs.sentry.io/clients/java/modules/logback/
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
    "org.tpolecat" %% "doobie-hikari"    % doobieVersion,
    "org.tpolecat" %% "doobie-specs2"   % doobieVersion % "test"
  )

  val automation = common ++ List(
    "is.cir" %% "ciris-core" % circeVersion,
    "is.cir" %% "ciris-cats" % circeVersion,
    "is.cir" %% "ciris-cats-effect" % circeVersion
  )

  val server = common ++ List(
    "io.grpc" % "grpc-services" % grpcNettyVersion
  )
}
