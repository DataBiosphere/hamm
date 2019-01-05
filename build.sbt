lazy val ccm = project.in(file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(protobuf, core, automation, server)

val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

lazy val core =
  project
    .in(file("core"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings
    )

lazy val automation =
  project
    .in(file("automation"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings
    )
    .dependsOn(protobuf)
    .dependsOn(core)
    .dependsOn(server % "test->test;compile->compile")

lazy val server =
  project
    .in(file("server"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      libraryDependencies ++= Dependencies.server,
      Settings.serverSettings
    )
    .dependsOn(protobuf)
    .dependsOn(core % "test->test;compile->compile")
    .enablePlugins(JavaAppPackaging)