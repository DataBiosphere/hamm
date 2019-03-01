import sbt._

object Dependencies {
  val minitestVersion = "2.2.2"
  val circeVersion = "0.11.1"
  val http4sVersion = "0.20.0-M6"
  val grpcNettyVersion = "1.18.0"
  val cirisVersion = "0.12.1"
  val doobieVersion = "0.7.0-M2"

  val common = List(
    "io.monix" %% "minitest" % minitestVersion % "test",
    "io.monix" %% "minitest-laws" % minitestVersion % "test",
    "io.chrisdavenport" %% "log4cats-slf4j"   % "0.2.0",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s"      %% "http4s-dsl"          % http4sVersion,
    "com.github.pureconfig" %% "pureconfig" % "0.10.1",
    "io.sentry" % "sentry-logback" % "1.7.16", // see doc https://docs.sentry.io/clients/java/modules/logback/
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
    "org.tpolecat" %% "doobie-hikari"    % doobieVersion,
    "org.tpolecat" %% "doobie-specs2"   % doobieVersion % "test",
    "is.cir" %% "ciris-core" % cirisVersion,
    "is.cir" %% "ciris-cats" % cirisVersion,
    "is.cir" %% "ciris-cats-effect" % cirisVersion
  )

  val automation = common

  val costUpdater = common ++ List(
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.broadinstitute.dsde.workbench" %% "workbench-google2" % "0.1-7ae5c6d-SNAP" % "test->test;compile->compile",
    "io.grpc" % "grpc-core" % "1.17.1",
    "io.circe" %% "circe-fs2" % "0.11.0"
  )
  
  val server = common ++ List(
    "io.grpc" % "grpc-services" % grpcNettyVersion,
    "io.grpc" % "grpc-netty" % grpcNettyVersion
  )
}
