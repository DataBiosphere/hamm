package org.broadinstitute.workbench.hamm.costUpdater

import com.google.pubsub.v1.ProjectTopicName
import org.broadinstitute.dsde.workbench.google2.SubscriberConfig
import org.broadinstitute.workbench.hamm.HammTestSuite

import scala.concurrent.duration._

object ConfigTest extends HammTestSuite {
  test("Config should load configuration file properly"){
    val config = Config.appConfig
    val expectedConfig = CostUpdaterAppConfig(GoogleConfig(
      SubscriberConfig(
        "fakePathToSubscriberCredential",
        ProjectTopicName.of("fakeProjectName", "fakeTopicName"),
        30 seconds,
        None
      )))
    assertEquals(config, Right(expectedConfig))
  }
}