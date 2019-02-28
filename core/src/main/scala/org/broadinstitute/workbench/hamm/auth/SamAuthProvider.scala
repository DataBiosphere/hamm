package org.broadinstitute.workbench.hamm.auth


import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.config.SamConfig
import org.broadinstitute.workbench.hamm.model._

class SamAuthProvider(val config: SamConfig) extends SamProvider with HammLogger {

  val workflowCollectionResourceTypeName = "workflow-collection"
  val getCostResourceAction = "get_cost"

  def hasWorkflowCollectionPermission(token: String, samResource: SamResource): Boolean = {
    samClient.checkResourceAction(token, workflowCollectionResourceTypeName, samResource.resourceName, getCostResourceAction)
  }

}