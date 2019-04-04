package org.broadinstitute.dsp.workbench.hamm.server.auth

import org.broadinstitute.dsp.workbench.hamm.TestComponent
import org.broadinstitute.dsp.workbench.hamm.TestData
import org.broadinstitute.dsp.workbench.hamm.config.SamConfig
import org.scalatest.{FreeSpecLike, Matchers}
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsp.workbench.hamm.config.config.SamConfigReader
import SamAuthProviderSpec.samAuthProvider

class SamAuthProviderSpec extends FreeSpecLike with Matchers with TestComponent {
  "should check user has permissions on a workflow collection" in {
    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe false

    samAuthProvider.samClient.actionsPerResourcePerToken += (TestData.testSamResource, TestData.testToken) -> Set(TestData.testSamResourceAction)

    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe true
  }
}

object SamAuthProviderSpec extends TestComponent {
  val samAuthProvider: TestSamAuthProvider = new TestSamAuthProvider(config.as[SamConfig]("sam"))
}

class TestSamAuthProvider(samConfig: SamConfig) extends SamAuthProvider(samConfig) {
  override lazy val samClient = new MockSamSwaggerClient()
}