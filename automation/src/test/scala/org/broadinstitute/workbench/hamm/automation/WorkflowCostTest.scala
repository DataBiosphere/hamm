//package org.broadinstitute.workbench.hamm
//package automation
//
//import cats.effect.IO
//import fs2.Stream
//import io.grpc.Metadata
//import minitest.laws.Checkers
//import org.broadinstitute.workbench.hamm.protos.hamm._
////import org.lyranthe.fs2_grpc.java_runtime.implicits._
////import server.Generators._
//
//object WorkflowCostTest extends HammTestSuite with Checkers {
//  val defaultMetaData = new Metadata()
//  val authKey = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
//  import sys.process._
//  val token: String = "gcloud auth print-access-token".!!
//  defaultMetaData.put(authKey, token)
//
////  TODO: put this in a different file
//  test("status should return build info"){
//    val res = for {
//      managedChannel <- Stream.resource(TestDependencies.managedChannelResource)
//      ccmStub = HammFs2Grpc.stub[IO](managedChannel)
//      response <- Stream.eval(ccmStub.status(StatusRequest(), defaultMetaData))
//    } yield {
//      assert(response.sbtVersion.contains("1.2.8"))
//    }
//    res.compile.last.unsafeRunSync().get
//  }
//
//  test("workflow getCost should calculate compute cost"){
//    val res = for {
//      managedChannel <- Stream.resource(TestDependencies.managedChannelResource)
//      workflowStub = HammFs2Grpc.stub[IO](managedChannel)
//      response <- Stream.eval(workflowStub.getWorkflowCost(WorkflowCostRequest(""), defaultMetaData))
//    } yield {
//      val expectedResponse = WorkflowCostResponse(0.6596219178082192)
//      assert(response == expectedResponse)
//    }
//    res.compile.last.unsafeRunSync().get
//  }
//
//  test("authendpoint should return user info") {
//    val res = for {
//      managedChannel <- Stream.resource(TestDependencies.managedChannelResource)
//      workflowStub = HammFs2Grpc.stub[IO](managedChannel)
//      response <- Stream.eval(workflowStub.authedEndpoint(AuthedEndpointRequest("hello"), defaultMetaData))
//    } yield {
//    }
//    res.compile.last.unsafeRunSync().get
//  }
//
//
//}
