package org.broadinstitute.workbench.hamm.auth


import org.broadinstitute.workbench.hamm.HammLogger
import org.broadinstitute.workbench.hamm.model._
import org.http4s.Uri

class SamAuthProvider(val samServer: Uri) extends SamProvider with HammLogger {

  val workflowCollectionResourceTypeName = "workflow-collection"
  val getCostResourceAction = "get_cost"

  def hasWorkflowCollectionPermission(token: String, samResource: SamResource): Boolean = {
    samClient.checkResourceAction(token, workflowCollectionResourceTypeName, samResource.resourceName, getCostResourceAction)
  }

}



//  def getUserStatus(token: String): SamUserInfoResponse = {
//    val url = uri + "/register/user/v2/self/info"
//    val request = GET(uri = Uri.unsafeFromString(url), Authorization(Credentials.Token(AuthScheme.Bearer, token)), Accept(MediaType.application.json))
//    httpClient.expect[SamUserInfoResponse](request).unsafeRunSync()
//
////    httpClient.expect[SamUserInfoResponse] (
////      Request[IO](
////        Method.GET,
////        Uri.unsafeFromString(uri + "/register/user/v2/self/info"),
////        headers = Headers(Header("authorization", token))))
////      .unsafeRunSync()
//
//  }