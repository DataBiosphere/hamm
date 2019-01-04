package org.broadinstitute.workbench.ccm

import cats.effect.IO
import fs2.Stream
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Metadata}
import org.broadinstitute.workbench.protos.ccm._
import minitest.laws.Checkers
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import Generators._

import scala.concurrent.ExecutionContext.Implicits.global

object WorkflowCostTest extends CcmTestSuite with Checkers {
  implicit val cs = IO.contextShift(global)
  val managedChannelStream: Stream[IO, ManagedChannel] =
    ManagedChannelBuilder
      .forAddress("127.0.0.1", 9999)
      .usePlaintext()
      .stream[IO]

  val defaultMetaData = new Metadata()

  test("workflow getCost should calculate compute cost"){
    check1{
      (workflowCostRequest: WorkflowCostRequest) =>
        val res = for {
          managedChannel <- managedChannelStream
          workflowStub = CcmFs2Grpc.stub[IO](managedChannel)
          response <- Stream.eval(workflowStub.getWorkflowCost(workflowCostRequest, defaultMetaData))
        } yield {
          val expectedResponse = WorkflowCostResponse(0.06899999999999999)
          response == expectedResponse
        }

        res.compile.last.unsafeRunSync().get
    }
  }
}
