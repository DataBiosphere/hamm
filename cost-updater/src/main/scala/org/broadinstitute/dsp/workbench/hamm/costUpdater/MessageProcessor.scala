package org.broadinstitute.dsp.workbench.hamm.costUpdater

import cats.effect.{Concurrent, Sync}
import cats.implicits._
import fs2.{Pipe, Stream}
import io.chrisdavenport.log4cats.Logger
import io.circe.Decoder
import org.broadinstitute.dsde.workbench.google2.{Event, GcsBlobName, GoogleStorageService, GoogleSubscriber}
import org.broadinstitute.dsde.workbench.model.google.GcsBucketName
import org.broadinstitute.dsp.workbench.hamm.model.CromwellMetadataJsonCodec.metadataResponseDecoder
import org.broadinstitute.dsp.workbench.hamm.model.MetadataResponse

class MessageProcessor[F[_]: Logger: Concurrent](subscriber: GoogleSubscriber[F, NotificationMessage], storage: GoogleStorageService[F]) {
  private[hamm] def parseNotification(notificationMessage: NotificationMessage): Stream[F, MetadataResponse] = {
    val metadataStream = storage.getObject(notificationMessage.bucketAndObject.bucketName, notificationMessage.bucketAndObject.blobName)
    metadataStream
      .through(fs2.compress.gunzip(4096)) //unzip the bytes
      .through(io.circe.fs2.byteStreamParser) //parse bytes into Json
      .through(io.circe.fs2.decoder[F, MetadataResponse]) //parse Json into MetadataResponse
  }

  private val updateCost: Pipe[F, Event[NotificationMessage], Unit] = in => {
    in.flatMap {
      event =>
        for {
          metadata <- parseNotification(event.msg)
          //TODO: calculate cost and persist to database
          _ <- Stream.eval(Sync[F].delay(event.consumer.ack()))
        } yield ()
    }
  }

  val process: Stream[F, Unit] = subscriber.messages through updateCost
}

object MessageProcessor {
  def apply[F[_]: Logger: Concurrent](subscriber: GoogleSubscriber[F, NotificationMessage], storage: GoogleStorageService[F]): MessageProcessor[F] = new MessageProcessor[F](subscriber, storage)

  implicit val bucketAndObjectDecoder: Decoder[BucketAndObject] = Decoder.decodeString.emap{
    s =>
      //selfLink has this format: https://www.googleapis.com/storage/v1/b/cromwell_metadata/o/qitest4
      val res = for {
        withoutPrefix <- Either.catchNonFatal(s.replaceAll("https://www.googleapis.com/storage/v1/b/", ""))
        parts <- Either.catchNonFatal(withoutPrefix.split("/o/"))
        bucketName <- Either.catchNonFatal(parts(0))
        objectName <- Either.catchNonFatal(parts(1))
      } yield BucketAndObject(GcsBucketName(bucketName), GcsBlobName(objectName))

      res.leftMap(_ => s"Invalid selfLink: $s")
  }

  implicit val notificationMessageDecoder: Decoder[NotificationMessage] = Decoder.forProduct1(
    "selfLink"
  )(NotificationMessage.apply)
}

final case class BucketAndObject(bucketName: GcsBucketName, blobName: GcsBlobName)
final case class NotificationMessage(bucketAndObject: BucketAndObject)