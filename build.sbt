lazy val root = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(protobuf, automation, server)

val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

lazy val automation =
  project
    .in(file("automation"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings
    )
    .dependsOn(protobuf)
    .dependsOn(server % "test->test;compile->compile")

lazy val server =
  project
    .in(file("server"))
    .settings(
      libraryDependencies ++= Dependencies.server,
      Settings.commonSettings
    )
    .dependsOn(protobuf)

trapExit := false