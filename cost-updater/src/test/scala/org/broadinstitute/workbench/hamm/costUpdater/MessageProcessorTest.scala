package org.broadinstitute.workbench.hamm.costUpdater

import org.broadinstitute.workbench.hamm.HammTestSuite
import MessageProcessor._
import io.circe.parser._
import Generators._
import cats.effect.IO
import com.google.cloud.storage.{Acl, BucketInfo}
import org.broadinstitute.dsde.workbench.google2.{Event, GcsBlobName, GoogleStorageService, GoogleSubscriber, RemoveObjectResult}
import org.broadinstitute.dsde.workbench.model.google.{GcsBucketName, GcsObjectName, GoogleProject}
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodecTest
import fs2.Stream
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
    check1{
      (notificationMessage: NotificationMessage) =>
        val messageProcessor = MessageProcessor[IO](fakeSubScriber, fakeStorage)
        val result = messageProcessor.parseNotification(notificationMessage).compile.lastOrError.unsafeRunSync()
        result == CromwellMetadataJsonCodecTest.expectedResponse
    }
  }

  val fakeStorage = new GoogleStorageService[IO]{
    override def listObjectsWithPrefix(
          bucketName: GcsBucketName,
          objectNamePrefix: String,
          maxPageSize: Long): fs2.Stream[IO, GcsObjectName] = ???
    override def storeObject(bucketName: GcsBucketName, objectName: GcsBlobName, objectContents: Array[Byte], objectType: String): IO[Unit] = ???
    override def setBucketLifecycle(bucketName: GcsBucketName, lifecycleRules: List[BucketInfo.LifecycleRule]): IO[Unit] = ???
    override def unsafeGetObject(bucketName: GcsBucketName, blobName: GcsBlobName): IO[Option[String]] = ???
    override def getObject(bucketName: GcsBucketName, blobName: GcsBlobName): Stream[IO, Byte] = Stream.emits(CromwellMetadataJsonCodecTest.sampleTest.getBytes("UTF-8")).covary[IO]
    override def removeObject(bucketName: GcsBucketName, objectName: GcsBlobName): IO[RemoveObjectResult] = ???
    override def createBucket(billingProject: GoogleProject, bucketName: GcsBucketName, acl: List[Acl]): IO[Unit] = ???
  }

  val fakeSubScriber = new GoogleSubscriber[IO, NotificationMessage] {
    def messages: Stream[IO, Event[NotificationMessage]] = Stream.empty
    def start: IO[Unit] = ???
    def stop: IO[Unit] = ???
  }
}