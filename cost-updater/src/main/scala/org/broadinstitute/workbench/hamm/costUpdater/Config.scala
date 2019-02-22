package org.broadinstitute.workbench.hamm
package costUpdater

import cats.implicits._
import com.google.pubsub.v1.ProjectTopicName
import org.broadinstitute.dsde.workbench.google2.SubscriberConfig
import org.broadinstitute.dsde.workbench.model.google.GcsBucketName
import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.error.ExceptionThrown
import pureconfig.generic.auto._

object Config {
  implicit val projectTopicNameConfigReader: ConfigReader[ProjectTopicName] = ConfigReader.fromCursor{
    cursor =>
      for{
        objCur <- cursor.asObjectCursor
        projectCur <- objCur.atKey("project-name")
        project <- projectCur.asString
        topicCur <- objCur.atKey("topic-name")
        topic <- topicCur.asString
      } yield ProjectTopicName.of(project, topic)
  }

  implicit val uriConfigReader: ConfigReader[Uri] = ConfigReader.fromString(
    s => Uri.fromString(s).leftMap(err => ExceptionThrown(err))
  )

  implicit val gcsBucketNameConfigReader: ConfigReader[GcsBucketName] = ConfigReader.fromString(
    s => Right(GcsBucketName(s))
  )

  val appConfig = pureconfig.loadConfig[CostUpdaterAppConfig].leftMap(failures => new RuntimeException(failures.toList.map(_.description).mkString("\n")))
}

final case class GoogleConfig(subscriber: SubscriberConfig, metadataNotification: MetadataNotificationCreaterConfig)

final case class CostUpdaterAppConfig(google: GoogleConfig)