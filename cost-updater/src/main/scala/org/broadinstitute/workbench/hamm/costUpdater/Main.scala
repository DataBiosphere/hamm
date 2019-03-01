package org.broadinstitute.workbench.hamm
package costUpdater

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import fs2._
import fs2.concurrent.InspectableQueue
import org.broadinstitute.dsde.workbench.google2.{Event, GoogleSubscriber}
import org.broadinstitute.workbench.hamm.model.CromwellMetadataJsonCodec.metadataResponseDecoder
import org.broadinstitute.workbench.hamm.model._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    implicit val logger = Slf4jLogger.unsafeCreate[IO]

    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)

      _ <- Stream.eval(logger.info("Starting Hamm Cost Updater Grpc server"))

      credential <- Stream.resource(org.broadinstitute.dsde.workbench.google2.credentialResource[IO](appConfig.google.subscriber.pathToCredentialJson))

      queue <- Stream.eval(InspectableQueue.bounded[IO, Event[MetadataResponse]](10))
      subscriber <- Stream.resource(GoogleSubscriber.resource(appConfig.google.subscriber, queue))
      messageProcessor = MessageProcessor[IO](subscriber, appConfig.google.subscriber.projectTopicName)

      service = CostUpdaterService[IO](queue).service
      serverStream = BlazeServerBuilder[IO]
        .bindHttp(8080, "127.0.0.1")
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