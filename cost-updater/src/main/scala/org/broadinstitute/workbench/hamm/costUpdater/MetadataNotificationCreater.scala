package org.broadinstitute.workbench.hamm.costUpdater

import cats.effect.{Resource, Sync}
import com.google.auth.oauth2.{GoogleCredentials, ServiceAccountCredentials}
import com.google.pubsub.v1.ProjectTopicName
import io.circe.Encoder
import org.broadinstitute.dsde.workbench.model.google.GcsBucketName
import org.broadinstitute.workbench.hamm.costUpdater.MetadataNotificationCreater._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Headers, Method, Request, Uri}
import com.google.api.services.storage.StorageScopes

class MetadataNotificationCreater[F[_]: Sync](httpClient: Client[F], config: MetadataNotificationCreaterConfig) extends Http4sClientDsl[F] {
  def createNotification(topic: ProjectTopicName): F[Unit] = credentialResourceWithScope(config.pathToCredentialJson).use{
    serviceAccountCredentials =>
      val notificationUri = config.googleUrl.withPath(s"/storage/v1/b/${config.metadataBucketName.value}/notificationConfigs")
      val notificationBody = NotificationRequest(topic, "JSON_API_V1")
      val request = Request[F](
        method = Method.POST,
        uri = notificationUri,
        headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, serviceAccountCredentials.refreshAccessToken().getTokenValue)))
      ).withEntity(notificationBody)

      httpClient.expect[Unit](request)
  }

  private def credentialResourceWithScope(pathToCredential: String): Resource[F, GoogleCredentials] = for {
    credentialFile <- org.broadinstitute.dsde.workbench.util.readFile(pathToCredential)
    credential <- Resource.liftF(Sync[F].delay(ServiceAccountCredentials.fromStream(credentialFile).createScoped(StorageScopes.all())))
  } yield credential
}

object MetadataNotificationCreater {
  implicit val projectTopicNameEncoder: Encoder[ProjectTopicName] = Encoder.encodeString.contramap(
    projectTopicName =>
      s"projects/${projectTopicName.getProject}/topics/${projectTopicName.getTopic}"
  )

  implicit val notificationRequestEncoder: Encoder[NotificationRequest] = Encoder.forProduct2(
    "topic",
    "payload_format"
  )(x => NotificationRequest.unapply(x).get)
}

final case class NotificationRequest(topic: ProjectTopicName, payloadFormat: String)
final case class MetadataNotificationCreaterConfig(pathToCredentialJson: String, googleUrl: Uri, metadataBucketName: GcsBucketName)