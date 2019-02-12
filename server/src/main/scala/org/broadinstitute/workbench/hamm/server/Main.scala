package org.broadinstitute.workbench.hamm
package server

import cats.effect._
import cats.implicits._
import org.broadinstitute.workbench.hamm.protos.hamm._
import io.grpc._
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.grpc.protobuf.services.ProtoReflectionService
import fs2._
import org.broadinstitute.workbench.hamm.auth.HttpSamDAO
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.http4s.Uri
import org.http4s.client.blaze._

import scala.concurrent.ExecutionContext.Implicits.global //use better thread pool

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    implicit val logger = Slf4jLogger.unsafeCreate[IO]

    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)
      _ <- Stream.eval(logger.info("Starting Cloud Cost Management Grpc server"))
      httpClient <- BlazeClientBuilder[IO](global).stream
      //pricing = new GooglePriceListDAO[IO](httpClient, appConfig.pricingGoogleUrl)
      pricing = new GooglePriceListDAO[IO](httpClient, Uri.unsafeFromString("https://cloudbilling.googleapis.com")) // ToDo: put this in config
      metadataDAO = new WorkflowMetadataDAO[IO](httpClient, Uri.unsafeFromString("https://cromwell.dsde-dev.broadinstitute.org/api/workflows/v1")) // ToDo: put this in config
      samDAO = new HttpSamDAO[IO](httpClient, Uri.unsafeFromString("https://sam.dsde-dev.broadinstitute.org")) // ToDo: put this in config
      ccmService: ServerServiceDefinition = HammFs2Grpc.bindService(new WorkflowCostService[IO](pricing, metadataDAO, samDAO))
      _ <- ServerBuilder.forPort(9999)
        .addService(ccmService)
        .addService(ProtoReflectionService.newInstance())
        .stream[IO]
        .evalMap(server => IO(server.start()))
    } yield ()

    app.handleErrorWith(error => Stream.eval(logger.error(error)("Failed to start server")))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
