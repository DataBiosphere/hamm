package org.broadinstitute.workbench

import cats.effect.IO
import org.broadinstitute.workbench.ccm.protos.workflow._
import fs2._
import _root_.io.grpc._
import cats.implicits._
import fs2.StreamApp.ExitCode
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import io.grpc.StatusRuntimeException

//object Main extends IOApp {
object Main extends StreamApp[IO] {
  val managedChannelStream: Stream[IO, ManagedChannel] =
    ManagedChannelBuilder
      .forAddress("127.0.0.1", 9999)
      .usePlaintext()
      .stream[IO]

  def runProgram(stub: WorkflowFs2Grpc[IO]): IO[Unit] = {
    for {
      response <- stub.getCost(WorkflowCostRequest(1), new Metadata()).attempt
      _ <- IO(println(response.leftMap{case t: StatusRuntimeException => t.getStatus}))
    } yield ()
  }

//  override def run(
//      args: List[String]): IO[ExitCode] =
  override def stream(
      args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    val res = for {
      managedChannel <- managedChannelStream
      workflowStub = WorkflowFs2Grpc.stub[IO](managedChannel)
      _ <- Stream.eval(runProgram(workflowStub))
    } yield ()

    res.as(ExitCode.Success)//.compile.drain.as(ExitCode.Success)
  }
}
