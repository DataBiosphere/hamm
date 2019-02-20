//package org.broadinstitute.workbench.hamm.service
//
//
//import io.grpc.Metadata
//import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
//import org.broadinstitute.workbench.hamm.model._
//
//abstract class AuthedService(samDAO: HttpSamDAO) {
//  //implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
//  private val bearerPattern = """(?i)bearer (.*)""".r
//
//  def checkAuthorization(workflowCollection: WorkflowCollectionId, action: String, token: String): Boolean = {
//    samDAO.queryAction(SamResource(workflowCollection.uuid.toString), action, token)
//  }
//
//  def withAuthenticatedUser[T](clientHeaders: Metadata)(f: UserInfo => T): T = {
//    val token = clientHeaders.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER))
//    val status = samDAO.getUserStatus(token)
//    f(UserInfo.apply(status, token))
//  }
//
//}
