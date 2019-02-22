package org.broadinstitute.workbench.hamm.costUpdater

import cats.effect.{Concurrent, Sync}
import com.google.pubsub.v1.ProjectTopicName
import fs2.{Pipe, Stream}
import io.chrisdavenport.log4cats.Logger
import org.broadinstitute.dsde.workbench.google2.{Event, GoogleSubscriber}
import org.broadinstitute.workbench.hamm.model.MetadataResponse

class MessageProcessor[F[_]: Logger: Concurrent](subscriber: GoogleSubscriber[F, MetadataResponse], projectTopicName: ProjectTopicName) {
  private val updateCost: Pipe[F, Event[MetadataResponse], Unit] = in => {
    in.evalMap{
      event =>
        //TODO: calculate cost and persist to database before acknowledging
        Sync[F].delay(event.consumer.ack())
    }
  }

  val process: Stream[F, Unit] = subscriber.messages through updateCost
}

object MessageProcessor {
  def apply[F[_]: Logger: Concurrent](subscriber: GoogleSubscriber[F, MetadataResponse], projectTopicName: ProjectTopicName): MessageProcessor[F] = new MessageProcessor[F](subscriber, projectTopicName)
}