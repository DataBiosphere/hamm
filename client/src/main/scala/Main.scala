package org.broadinstitute.workbench

import cats.effect.{ExitCode, IO, IOApp}
import org.broadinstitute.workbench.ccm.protos.workflow._
import fs2._
import io.grpc._
import cats.implicits._
import org.lyranthe.fs2_grpc.java_runtime.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import io.grpc.StatusRuntimeException

object Main extends IOApp {
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

  override def run(
      args: List[String]): IO[ExitCode] = {
    val res = for {
      managedChannel <- managedChannelStream
      workflowStub = WorkflowFs2Grpc.stub[IO](managedChannel)
      _ <- Stream.eval(runProgram(workflowStub))
    } yield ()

    res.compile.drain.as(ExitCode.Success)
  }
}
