package org.broadinstitute.dsp.workbench.hamm.costUpdater

import com.google.pubsub.v1.ProjectTopicName
import org.broadinstitute.dsde.workbench.google2.SubscriberConfig
import org.broadinstitute.dsde.workbench.hamm.costUpdater._
import org.broadinstitute.dsp.workbench.hamm.HammTestSuite

import scala.concurrent.duration._
import scala.concurrent.duration._

object ConfigTest extends HammTestSuite {
  test("Config should load configuration file properly"){
    val config = Config.appConfig
    val expectedConfig = CostUpdaterAppConfig(GoogleConfig(SubscriberConfig(
      "fakePathToSubscriberCredential",
      ProjectTopicName.of("fakeProjectName", "fakeTopicName"),
      30 seconds
    )))
    assertEquals(config, Right(expectedConfig))
  }
}
