package org.broadinstitute.workbench.hamm.auth

import org.broadinstitute.workbench.hamm.TestComponent
import org.broadinstitute.workbench.hamm.model.{SamResource, SamResourceAction}
import org.http4s.AuthScheme
import org.http4s.Credentials.Token
import org.scalatest.{FreeSpecLike, Matchers}



class SamAuthProviderSpec extends FreeSpecLike with Matchers with TestComponent {


  "should check user has permissions on a workflow collection" in {
    val samAuthProvider = getSamAuthProvider
    val token = Token(AuthScheme.Bearer, "fake-token")
    val samResource = SamResource("fake-wf-collection-id")
    val samResourceAction = SamResourceAction("get_cost")

    samAuthProvider.hasWorkflowCollectionPermission(token, samResource) shouldBe false

    samAuthProvider.samClient.actionsPerResourcePerToken += (samResource, token) -> Set(samResourceAction)

    samAuthProvider.hasWorkflowCollectionPermission(token, samResource) shouldBe true
  }


}
