package org.broadinstitute.dsp.workbench.hamm.auth

import org.broadinstitute.dsde.workbench.client.sam.api.ResourcesApi
import org.broadinstitute.dsde.workbench.client.sam.ApiClient
import org.broadinstitute.dsp.workbench.hamm.model.{SamResource, SamResourceAction, SamResourceType}
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import org.http4s.Credentials.Token
import org.http4s.Uri

class SamSwaggerClient(samBasePath: Uri) extends HammLogger {

  private[auth] def samResourcesApi(accessToken: String): ResourcesApi = {
    val apiClient = new ApiClient()
    apiClient.setAccessToken(accessToken)
    apiClient.setBasePath(samBasePath.renderString)
    new ResourcesApi(apiClient)
  }

  def checkResourceAction(token: Token, samResourceType: SamResourceType, samResource: SamResource, action: SamResourceAction):Boolean = {
    val samResourceApi = samResourcesApi(token.token)
    val result = samResourceApi.resourceAction(samResourceType.asString, samResource.asString, action.asString)
    result
  }


}
