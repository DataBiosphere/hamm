package org.broadinstitute.dsde.workbench.hamm.auth

import org.broadinstitute.dsde.workbench.hamm.{TestComponent, TestData}
import org.scalatest.{FreeSpecLike, Matchers}



class SamAuthProviderSpec extends FreeSpecLike with Matchers with TestComponent {


  "should check user has permissions on a workflow collection" in {
    val samAuthProvider = getSamAuthProvider

    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe false

    samAuthProvider.samClient.actionsPerResourcePerToken += (TestData.testSamResource, TestData.testToken) -> Set(TestData.testSamResourceAction)

    samAuthProvider.hasWorkflowCollectionPermission(TestData.testToken, TestData.testSamResource) shouldBe true
  }


}
