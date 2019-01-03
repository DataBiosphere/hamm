package org.broadinstitute.workbench.ccm

import cats.effect._
import cats.implicits._
import fs2.StreamApp.ExitCode
import org.broadinstitute.workbench.ccm.protos.workflow._
import io.grpc._
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import io.grpc.protobuf.services.ProtoReflectionService
import fs2._
import org.broadinstitute.workbench.ccm.google.Pricing
import org.http4s.client.blaze.Http1Client
//import org.http4s.client.blaze._
import scala.concurrent.ExecutionContext.Implicits.global //use better thread pool

//object Main extends IOApp {
object Main extends StreamApp[IO] {
// Commented out code is compatible with newer version of fs2, but fs2-grpc's latest stable version is on older version of fs2
//  override def run(args: List[String]): IO[ExitCode] =  {
  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, ExitCode] = {

  val httpClient = Http1Client[IO]().unsafeRunSync //TODO: upgrade http4s and make this safe
    val app: Stream[IO, Unit] = for {
      _ <- Stream.eval(IO(println("Starting Cloud Cost Management Grpc server"))) //TODO: use logging
//      httpClient <- BlazeClientBuilder[IO](global).stream
      pricing = new Pricing[IO](httpClient)
      workflowCostService: ServerServiceDefinition = WorkflowFs2Grpc.bindService(new WorkflowImp[IO](pricing))
      _ <- ServerBuilder.forPort(9999)
        .addService(workflowCostService)
        .addService(ProtoReflectionService.newInstance())
        .stream[IO]
        .evalMap(server => IO(server.start()))
    } yield ()

    app.evalMap(_ => IO.never)//.compile.drain.as(ExitCode.Success)
  }
}