lazy val root = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(protobuf, client, server)

val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

lazy val client =
  project
    .in(file("client"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0"
      ),
      Settings.commonSettings
    )
    .dependsOn(protobuf)

val http4sVersion = "0.20.0-M4"

lazy val server =
  project
    .in(file("server"))
    .settings(
      libraryDependencies ++= List(
        "io.grpc" % "grpc-netty" % "1.11.0",
        "io.grpc" % "grpc-services" % "1.11.0",
        "io.circe" %% "circe-core" % "0.10.0",
        "org.http4s" %% "http4s-circe" % http4sVersion,
        "org.http4s" %% "http4s-blaze-client" % http4sVersion
      ),
      Settings.commonSettings
    )
    .dependsOn(protobuf)

trapExit := false