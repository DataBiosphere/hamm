package org.broadinstitute.dsp.workbench.hamm.server.auth

import org.broadinstitute.dsp.workbench.hamm.{TestComponent, TestData}
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamAuthProviderSpec.samAuthProvider
import org.http4s.Uri
import org.scalatest.{FreeSpecLike, Matchers}

class SamAuthProviderSpec extends FreeSpecLike with Matchers with TestComponent {
  "should check user has permissions on a workflow collection" in {
    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe false

    samAuthProvider.samClient.actionsPerResourcePerToken += (TestData.testSamResource, TestData.testToken) -> Set(TestData.testSamResourceAction)

    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe true
  }
}

object SamAuthProviderSpec {
  val samAuthProvider: TestSamAuthProvider = new TestSamAuthProvider(SamConfig(Uri.unsafeFromString("https://sam.dsde-dev.broadinstitute.org:443")))
}

class TestSamAuthProvider(samConfig: SamConfig) extends SamAuthProvider(samConfig) {
  override lazy val samClient = new MockSamSwaggerClient()
}