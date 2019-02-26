package org.broadinstitute.workbench.hamm.auth

import io.swagger.client.ApiClient
import io.swagger.client.api.ResourcesApi
import org.broadinstitute.workbench.hamm.HammLogger
import org.http4s.Uri

class SamSwaggerClient(samBasePath: Uri) extends HammLogger {

  private[auth] def samResourcesApi(accessToken: String): ResourcesApi = {
    logger.info("samBasePath.renderString " + samBasePath.renderString)
    val apiClient = new ApiClient()
    apiClient.setAccessToken(accessToken)
    apiClient.setBasePath(samBasePath.renderString)
    new ResourcesApi(apiClient)
  }

  def checkResourceAction(token: String, samResourceType: String, samResource: String, action: String):Boolean = {
    val samResourceApi = samResourcesApi(token)
    samResourceApi.resourceAction(samResourceType, samResource, action)
  }


}
