package org.broadinstitute.dsp.workbench.hamm.costUpdater

import org.broadinstitute.dsp.workbench.hamm.HammTestSuite
import MessageProcessor._
import io.circe.parser._
import Generators._
import cats.data.NonEmptyList
import cats.effect.IO
import com.google.cloud.Identity
import com.google.cloud.storage.{Acl, BucketInfo}
import org.broadinstitute.dsde.workbench.google2.{Event, GcsBlobName, GoogleStorageService, GoogleSubscriber, RemoveObjectResult, StorageRole}
import org.broadinstitute.dsde.workbench.model.google.{GcsBucketName, GcsObjectName, GoogleProject}
import org.broadinstitute.dsp.workbench.hamm.model.CromwellMetadataJsonCodecTest
import fs2.Stream
import org.broadinstitute.dsde.workbench.model.TraceId

object MessageProcessorTest extends HammTestSuite {
  test("MessageProcessor should be able to decode NotificationMessage properly"){
    check1 {
      (notificationMessage: NotificationMessage) =>
        val sampleNotificationMessage =
          s"""
            |{
            |	"selfLink": "https://www.googleapis.com/storage/v1/b/${notificationMessage.bucketAndObject.bucketName.value}/o/${notificationMessage.bucketAndObject.blobName.value}"
            |}
          """.stripMargin

        val result = for {
          json <- parse(sampleNotificationMessage)
          r <- json.as[NotificationMessage]
        } yield r

        result == Right(notificationMessage)
    }
  }

  test("MessageProcessor should be able to parse NotificationMessage to metadata"){
    check1 {
      (notificationMessage: NotificationMessage) =>
        val messageProcessor = MessageProcessor[IO](fakeSubScriber, fakeStorage)
        val result = messageProcessor.parseNotification(notificationMessage).compile.lastOrError.unsafeRunSync()
        result == CromwellMetadataJsonCodecTest.sampleResponse
    }
  }

  val fakeStorage = new GoogleStorageService[IO]{
      override def listObjectsWithPrefix(
            bucketName: GcsBucketName,
            objectNamePrefix: String,
            maxPageSize: Long,
            traceId: Option[TraceId])
          : Stream[IO, GcsObjectName] = ???
      override def storeObject(bucketName:  GcsBucketName, objectName:  GcsBlobName, objectContents:  Array[Byte], objectType:  String, traceId:  Option[TraceId]): IO[Unit] = ???
      override def setBucketLifecycle(bucketName:  GcsBucketName, lifecycleRules:  List[BucketInfo.LifecycleRule], traceId:  Option[TraceId]): Stream[IO, Unit] = ???
      override def unsafeGetObject(bucketName:  GcsBucketName, blobName:  GcsBlobName, traceId:  Option[TraceId]): IO[Option[String]] = ???
      override def getObject(bucketName:  GcsBucketName, blobName:  GcsBlobName, traceId:  Option[TraceId]): Stream[IO, Byte] = ???
      override def removeObject(bucketName:  GcsBucketName, objectName:  GcsBlobName, traceId:  Option[TraceId]): IO[RemoveObjectResult] = ???
      override def createBucket(googleProject:  GoogleProject, bucketName:  GcsBucketName, acl:  Option[NonEmptyList[Acl]], traceId:  Option[TraceId]): Stream[IO, Unit] = ???
      override def setIamPolicy(bucketName:  GcsBucketName, roles:  Map[StorageRole, NonEmptyList[Identity]], traceId:  Option[TraceId]): Stream[IO, Unit] = ???
  }

  val fakeSubScriber = new GoogleSubscriber[IO, NotificationMessage] {
    def messages: Stream[IO, Event[NotificationMessage]] = Stream.empty
    def start: IO[Unit] = ???
    def stop: IO[Unit] = ???
  }
}