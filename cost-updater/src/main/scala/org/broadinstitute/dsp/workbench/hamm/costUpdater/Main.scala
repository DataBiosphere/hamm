package org.broadinstitute.dsp.workbench.hamm
package costUpdater

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import fs2._
import fs2.concurrent.InspectableQueue
import org.broadinstitute.dsde.workbench.google2.{Event, GoogleStorageService, GoogleSubscriber}
import org.broadinstitute.dsde.workbench.util.ExecutionContexts
import MessageProcessor.notificationMessageDecoder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    implicit val logger = Slf4jLogger.getLogger[IO]

    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)

      _ <- Stream.eval(logger.info("Starting Hamm Cost Updater server"))

      blockingExecutionContext <- Stream.resource(ExecutionContexts.fixedThreadPool[IO](appConfig.threadPool.blockingSize)) //scala.concurrent.blocking has default max extra thread number 256, so use this number to start with
      storage <- Stream.resource(GoogleStorageService.resource(appConfig.google.subscriber.pathToCredentialJson, blockingExecutionContext))

      queue <- Stream.eval(InspectableQueue.bounded[IO, Event[NotificationMessage]](appConfig.eventQueueSize))
      subscriber <- Stream.resource(GoogleSubscriber.resource(appConfig.google.subscriber, queue))

      messageProcessor = MessageProcessor[IO](subscriber, storage)

      service = CostUpdaterService(queue).service
      serverStream = BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(Router("/" -> service).orNotFound)
        .serve
        .void
       _ <- Stream(Stream.eval(subscriber.start), messageProcessor.process, serverStream).parJoin(3)
    } yield ()

    app.handleErrorWith(error => Stream.eval(logger.error(error)("Failed to start server")))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
