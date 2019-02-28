coverageExcludedPackages := "org.broadinstitute.workbench.hamm.protos"
coverageMinimum := 15 //Update this once there're more tests
coverageFailOnMinimum := true

lazy val hamm = project.in(file("."))
  .settings(
    skip in publish := true,
    Settings.commonSettings
  )
  .aggregate(core, costUpdater)


val core =
  project
    .in(file("core"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings,
      Settings.buildInfoSettings
    )

lazy val costUpdater =
  project
    .in(file("cost-updater"))
    .enablePlugins(JavaAppPackaging)
    .settings(
      libraryDependencies ++= Dependencies.costUpdater,
      Settings.costUpdaterSettings
    )
//    .dependsOn(protobuf)
    .dependsOn(core % "test->test;compile->compile")

lazy val automation =
  project
    .in(file("automation"))
    .settings(
      libraryDependencies ++= Dependencies.automation,
      Settings.commonSettings,
      Settings.buildInfoSettings
    )
//    .dependsOn(protobuf)
    .dependsOn(core)
 //   .dependsOn(server % "test->test;compile->compile")