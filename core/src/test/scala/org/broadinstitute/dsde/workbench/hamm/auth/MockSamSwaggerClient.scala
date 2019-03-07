package org.broadinstitute.dsde.workbench.hamm.auth

import org.broadinstitute.dsde.workbench.hamm.model.{SamResource, SamResourceAction, SamResourceType}
import org.http4s.Credentials.Token
import org.http4s.Uri

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

class MockSamSwaggerClient extends SamSwaggerClient(Uri.unsafeFromString("fake/path")) {

  val actionsPerResourcePerToken: mutable.Map[(SamResource, Token), Set[SamResourceAction]] = new TrieMap()

  override def checkResourceAction(token: Token, samResourceType: SamResourceType, samResource: SamResource, action: SamResourceAction): Boolean = {
    actionsPerResourcePerToken.get((samResource, token)).map(_.contains(action)).getOrElse(false)
  }

}
