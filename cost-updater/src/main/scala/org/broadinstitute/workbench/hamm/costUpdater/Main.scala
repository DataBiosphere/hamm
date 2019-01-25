package org.broadinstitute.workbench.hamm
package costUpdater

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.grpc._
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.grpc.protobuf.services.ProtoReflectionService
import fs2._
import org.broadinstitute.workbench.hamm.protos.costUpdater._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =  {
    implicit val logger = Slf4jLogger.unsafeCreate[IO]

    val app: Stream[IO, Unit] = for {
      _ <- Stream.eval(logger.info("Starting Cloud Cost Management Grpc server"))
      service: ServerServiceDefinition = CostUpdaterFs2Grpc.bindService(new CostUpdaterGrpcImp[IO])
      _ <- ServerBuilder.forPort(9999)
        .addService(service)
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

class CostUpdaterGrpcImp[F[_]: Sync: Logger] extends CostUpdaterFs2Grpc[F] {
  override def status(request: CostUpdaterStatusRequest, clientHeaders: Metadata): F[CostUpdaterStatusResponse] = Sync[F].point(CostUpdaterStatusResponse(
    BuildInfo.scalaVersion,
    BuildInfo.sbtVersion,
    BuildInfo.gitHeadCommit.getOrElse("No commit yet"),
    BuildInfo.buildTime,
    BuildInfo.toString
  ))
}