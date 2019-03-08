package org.broadinstitute.dsde.workbench.hamm.auth


import org.broadinstitute.dsde.workbench.hamm.HammLogger
import org.broadinstitute.dsde.workbench.hamm.config.SamConfig
import org.broadinstitute.dsde.workbench.hamm.model._
import org.http4s.Credentials.Token

class SamAuthProvider(val config: SamConfig) extends HammLogger {

  protected lazy val samClient = new SamSwaggerClient(config.samUrl)

  private val workflowCollectionResourceTypeName = SamResourceType("workflow-collection")
  private val getCostResourceAction = SamResourceAction("get_cost")

  def hasWorkflowCollectionPermission(token: Token, samResource: SamResource): Boolean = {
    samClient.checkResourceAction(token, workflowCollectionResourceTypeName, samResource, getCostResourceAction)
  }

}