package org.broadinstitute.dsp.workbench.hamm
package costUpdater

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.concurrent.InspectableQueue
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.broadinstitute.dsde.workbench.google2.{Event, GoogleStorageService, GoogleSubscriber}
import org.broadinstitute.dsde.workbench.util.ExecutionContexts
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    implicit val logger = Slf4jLogger.getLogger[IO]

    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)

      _ <- Stream.eval(logger.info("Starting Hamm Cost Updater Grpc server"))

      credential <- Stream.resource(org.broadinstitute.dsde.workbench.google2.credentialResource[IO](appConfig.google.subscriber.pathToCredentialJson))
      blockingExecutionContext <- Stream.resource(ExecutionContexts.fixedThreadPool[IO](256)) //scala.concurrent.blocking has default max extra thread number 256, so use this number to start with
      storage <- Stream.resource(GoogleStorageService.resource(appConfig.google.subscriber.pathToCredentialJson, blockingExecutionContext))

      queue <- Stream.eval(InspectableQueue.bounded[IO, Event[NotificationMessage]](10))
      subscriber <- Stream.resource(GoogleSubscriber.resource(appConfig.google.subscriber, queue))

      messageProcessor = MessageProcessor[IO](subscriber, storage)

      service = CostUpdaterService(queue).service
      serverStream = BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(Router("/" -> service).orNotFound)
        .serve
        .void
       _ <- Stream(Stream.eval(subscriber.start), messageProcessor.process, serverStream).parJoin(10) //TODO: potentially adjust maxOpen
    } yield ()

    app.handleErrorWith(error => Stream.eval(logger.error(error)("Failed to start server")))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
