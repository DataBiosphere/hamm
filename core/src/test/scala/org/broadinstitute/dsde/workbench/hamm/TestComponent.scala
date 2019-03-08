package org.broadinstitute.dsde.workbench.hamm


import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsde.workbench.hamm.api.HammRoutes
import org.broadinstitute.dsde.workbench.hamm.auth.{MockSamSwaggerClient, SamAuthProvider}
import org.broadinstitute.dsde.workbench.hamm.config.SamConfig
import org.broadinstitute.dsde.workbench.hamm.db._
import org.broadinstitute.dsde.workbench.hamm.service.{CostService, StatusService}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.Matchers

import scala.concurrent.ExecutionContext


trait TestComponent extends Matchers with ScalaFutures {
  implicit val executionContext: ExecutionContext = TestExecutionContext.testExecutionContext
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)


  val dbRef =  DbSingleton.ref

  val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())

  def getSamAuthProvider: TestSamAuthProvider = new TestSamAuthProvider(config.as[SamConfig]("sam"))

  val samAuthProvider = getSamAuthProvider

  val jobTable = new JobTable
  val workflowTable = new WorkflowTable

  val mockWorkflowTable = new MockWorkflowTable
  val mockJobTable = new MockJobTable(mockWorkflowTable)


  val costService = new CostService(samAuthProvider, dbRef, mockJobTable, mockWorkflowTable)

  val statusService = new StatusService()

  val hammRoutes = new HammRoutes(samAuthProvider, costService, statusService)


}

class TestSamAuthProvider(samConfig: SamConfig) extends SamAuthProvider(samConfig) {
  override lazy val samClient = new MockSamSwaggerClient()
}