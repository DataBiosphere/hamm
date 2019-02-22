import sbt._

object Dependencies {
  val circeVersion          = "0.10.0"
  val http4sVersion         = "0.20.0-M6"
  val grpcNettyVersion      = "1.18.0"
  val liquibaseVersion      = "3.5.3"
  val minitestVersion       = "2.2.2"
  val postgresDriverVersion = "42.2.4"
  val samV                  = "0.1-37bc074"
  val scalaTestVersion      = "3.0.5"
  val scalikejdbcVersion    = "3.3.2"
  val cirisVersion = "0.12.1"

  val common = List(
    "com.github.pureconfig"             %% "pureconfig"          % "0.10.1",
    "com.iheart"                        %% "ficus"               % "1.4.3",
    "com.zaxxer"                        %  "HikariCP"            % "1.3.+",
    "io.chrisdavenport"                 %% "log4cats-slf4j"      % "0.2.0",
    "io.circe"                          %% "circe-core"          % circeVersion,
    "io.circe"                          %% "circe-generic"       % circeVersion,
    "io.circe"                          %% "circe-parser"        % circeVersion,
    "io.grpc"                           %  "grpc-netty"          % grpcNettyVersion,
    "io.monix"                          %% "minitest"            % minitestVersion    % "test",
    "io.monix"                          %% "minitest-laws"       % minitestVersion    % "test",
    "io.sentry"                         %  "sentry-logback"      % "1.7.16",          // see doc https://docs.sentry.io/clients/java/modules/logback/
    "org.broadinstitute.dsde.workbench" %% "sam-client"          % samV,
    "org.http4s"                        %% "http4s-blaze-client" % http4sVersion,
    "org.http4s"                        %% "http4s-circe"        % http4sVersion,
    "org.http4s"                        %% "http4s-dsl"          % http4sVersion,
    "org.liquibase"                     %  "liquibase-core"      % liquibaseVersion,
    "org.log4s"                         %% "log4s"               % "1.7.0",
    "org.postgresql"                    %  "postgresql"          % postgresDriverVersion,
    "org.webjars"                       %  "webjars-locator"     % "0.34",
    "org.webjars"                       %  "swagger-ui"          % "3.17.3",
    "org.scalatest"                     %% "scalatest"           % scalaTestVersion     % "test",
    "org.scalikejdbc"                   %% "scalikejdbc"         % scalikejdbcVersion,
    "org.scalikejdbc"                   %% "scalikejdbc-config"  % scalikejdbcVersion,
    "org.scalikejdbc"                   %% "scalikejdbc-test"    % scalikejdbcVersion   % "test",
    "io.monix" %% "minitest" % minitestVersion % "test",
    "io.monix" %% "minitest-laws" % minitestVersion % "test",
    "io.chrisdavenport" %% "log4cats-slf4j"   % "0.2.0",
    "com.github.pureconfig" %% "pureconfig" % "0.10.1",
    "io.sentry" % "sentry-logback" % "1.7.16", // see doc https://docs.sentry.io/clients/java/modules/logback/
    "is.cir" %% "ciris-core" % cirisVersion,
    "is.cir" %% "ciris-cats" % cirisVersion,
    "is.cir" %% "ciris-cats-effect" % cirisVersion
  )

  val automation = common

  val costUpdater = common ++ List(
    "org.broadinstitute.dsde.workbench" %% "workbench-google2" % "0.1-36b0c79-SNAP"
  )
  
  val server = common ++ List(
    "org.broadinstitute.dsde.workbench" %% "workbench-google2" % "0.1-7ae5c6d-SNAP" % "test->test;compile->compile",
    "io.circe" %% "circe-fs2" % "0.11.0"
  )
}
