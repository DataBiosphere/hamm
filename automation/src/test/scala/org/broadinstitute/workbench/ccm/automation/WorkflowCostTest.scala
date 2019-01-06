package org.broadinstitute.workbench.ccm
package automation

import cats.effect.IO
import fs2.Stream
import io.grpc.Metadata
import minitest.laws.Checkers
import org.broadinstitute.workbench.ccm.protos.ccm._
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import server.Generators._

object WorkflowCostTest extends CcmTestSuite with Checkers {
  val defaultMetaData = new Metadata()
//  val authKey = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
//  import sys.process._
//  val token: String = "gcloud auth print-access-token".!!
//  defaultMetaData.put(authKey, s"Bearer $token")

//  TODO: put this in a different file
  test("status should return build info"){
    val res = for {
      managedChannel <- Stream.resource(TestDependencies.managedChannelResource)
      ccmStub = CcmFs2Grpc.stub[IO](managedChannel)
      response <- Stream.eval(ccmStub.status(StatusRequest(), defaultMetaData))
    } yield {
      assert(response.sbtVersion.contains("1.2.8"))
    }

    res.compile.last.unsafeRunSync().get
  }

  test("workflow getCost should calculate compute cost"){
    check1{
      (workflowCostRequest: WorkflowCostRequest) =>
        val res = for {
          managedChannel <- Stream.resource(TestDependencies.managedChannelResource)
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
