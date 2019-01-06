coverageExcludedPackages := "org.broadinstitute.workbench.ccm.protos"
coverageMinimum := 15 //Update this once there're more tests
coverageFailOnMinimum := true

lazy val ccm = project.in(file("."))
  .settings(
    skip in publish := true,
    Settings.commonSettings
  )
  .aggregate(core, protobuf, automation, server)

val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc, BuildInfoPlugin)
    .settings(Settings.buildInfoSettings)

val core =
  project
    .in(file("core"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings,
      Settings.buildInfoSettings
    )

lazy val server =
  project
    .in(file("server"))
    .enablePlugins(JavaAppPackaging)
    .settings(
      libraryDependencies ++= Dependencies.server,
      Settings.serverSettings
    )
    .dependsOn(protobuf)
    .dependsOn(core % "test->test;compile->compile")


lazy val automation =
  project
    .in(file("automation"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings,
      Settings.buildInfoSettings
    )
    .dependsOn(protobuf)
    .dependsOn(core)
    .dependsOn(server % "test->test;compile->compile")
