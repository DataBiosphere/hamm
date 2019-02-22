package org.broadinstitute.workbench.hamm
package costUpdater

import cats.effect.Sync
import cats.implicits._
import com.google.cloud.Identity
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.iam.v1.{Binding, Policy}
import com.google.pubsub.v1.ProjectTopicName
import fs2.Stream
import io.chrisdavenport.log4cats.Logger

class TopicCreator[F[_]: Logger: Sync](topicAdminClient: TopicAdminClient, projectTopicName: ProjectTopicName) {
  val create: Stream[F, Unit] = for{
    _ <- Stream.eval(Sync[F].delay(topicAdminClient.createTopic(projectTopicName)).void).handleErrorWith {
      case _: com.google.api.gax.rpc.AlreadyExistsException =>
        Stream.eval(Logger[F].debug(s"${projectTopicName} topic already exists"))
    }
    topicName = ProjectTopicName.format(projectTopicName.getProject, projectTopicName.getTopic)

    policy <- Stream.eval(Sync[F].delay(topicAdminClient.getIamPolicy(topicName))) //getIamPolicy requires admin role, see https://cloud.google.com/pubsub/docs/access-control

    binding = Binding.newBuilder()
      .setRole("roles/pubsub.publisher")
      .addMembers(Identity.allAuthenticatedUsers().toString) //TODO: restrict memeber to just rawl's bucket owner if possible
      .build()
    updatedPolicy = Policy.newBuilder(policy).addBindings(binding).build()

    _ <- Stream.eval(Sync[F].delay(topicAdminClient.setIamPolicy(topicName, updatedPolicy)))
  } yield ()
}

object TopicCreator {
  def apply[F[_]: Logger: Sync](topicAdminClient: TopicAdminClient, projectTopicName: ProjectTopicName): TopicCreator[F] = new TopicCreator[F](topicAdminClient, projectTopicName)
}