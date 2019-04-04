package org.broadinstitute.dsp.workbench.hamm.costUpdater

import java.nio.charset.StandardCharsets

import cats.effect.IO
import fs2.Stream
import io.circe.parser._
import org.broadinstitute.dsde.workbench.google2.mock.FakeGoogleStorageInterpreter
import org.broadinstitute.dsde.workbench.google2.{Event, GoogleSubscriber}
import org.broadinstitute.dsp.workbench.hamm.HammTestSuite
import org.broadinstitute.dsp.workbench.hamm.costUpdater.Generators._
import org.broadinstitute.dsp.workbench.hamm.costUpdater.MessageProcessor._
import org.broadinstitute.dsp.workbench.hamm.model.CromwellMetadataJsonCodecTest

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
        val messageProcessor = MessageProcessor[IO](fakeSubScriber, FakeGoogleStorageInterpreter)
        val messageBody = CromwellMetadataJsonCodecTest.sampleTest.getBytes(StandardCharsets.UTF_8)
        val gzipped = (fs2.Stream.emits(messageBody) through fs2.compress.gzip[fs2.Pure](2048)).compile.toList.toArray
        val result = for {
          _ <- FakeGoogleStorageInterpreter.storeObject(notificationMessage.bucketAndObject.bucketName, notificationMessage.bucketAndObject.blobName, gzipped, "text/plain")
          response <- messageProcessor.parseNotification(notificationMessage).compile.lastOrError
        } yield {
          response == CromwellMetadataJsonCodecTest.sampleResponse
        }

        result.unsafeRunSync()
    }
  }

  val fakeSubScriber = new GoogleSubscriber[IO, NotificationMessage] {
    def messages: Stream[IO, Event[NotificationMessage]] = Stream.empty
    def start: IO[Unit] = ???
    def stop: IO[Unit] = ???
  }
}