package org.broadinstitute.workbench.ccm

import cats.effect._
import cats.implicits._
import org.broadinstitute.workbench.protos.ccm._
import io.grpc._
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.Logger
import io.grpc.protobuf.services.ProtoReflectionService
import fs2._
import org.broadinstitute.workbench.ccm.pricing.GcpPricing
import org.http4s.client.blaze._
import scala.concurrent.ExecutionContext.Implicits.global //use better thread pool

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)
      implicit0(logger: Logger[IO]) <- Stream.eval(Slf4jLogger.create[IO])
      _ <- Stream.eval(logger.info("Starting Cloud Cost Management Grpc server"))
      httpClient <- BlazeClientBuilder[IO](global).stream
      pricing = new GcpPricing[IO](httpClient, appConfig.pricingGoogleUrl)
      workflowCostService: ServerServiceDefinition = CcmFs2Grpc.bindService(new WorkflowImp[IO](pricing))
      _ <- ServerBuilder.forPort(9999)
        .addService(workflowCostService)
        .addService(ProtoReflectionService.newInstance())
        .stream[IO]
        .evalMap(server => IO(server.start()))
    } yield ()

    app.evalMap(_ => IO.never).compile.drain.as(ExitCode.Success)
  }
}