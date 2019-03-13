package org.broadinstitute.dsp.workbench.hamm

import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsp.workbench.hamm.auth.{MockSamSwaggerClient, SamAuthProvider}
import org.broadinstitute.dsp.workbench.hamm.config.{GoogleConfig, SamConfig}
import org.broadinstitute.dsp.workbench.hamm.config.config.SamConfigReader
import org.broadinstitute.dsp.workbench.hamm.db._
import org.broadinstitute.dsp.workbench.hamm.service.{CostService, StatusService}
import org.scalatest.Matchers

import scala.concurrent.ExecutionContext


trait TestComponent extends Matchers {
  implicit val executionContext: ExecutionContext = TestExecutionContext.testExecutionContext
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)


  val dbRef =  DbSingleton.ref

  val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())
  val googleConfig = config.as[GoogleConfig]("google")

  def getSamAuthProvider: TestSamAuthProvider = new TestSamAuthProvider(config.as[SamConfig]("sam"))

  val samAuthProvider = getSamAuthProvider

  val jobTable = new JobTable
  val workflowTable = new WorkflowTable

  val mockWorkflowTable = new MockWorkflowTable
  val mockJobTable = new MockJobTable(mockWorkflowTable)


  val costService = new CostService(samAuthProvider, dbRef, mockJobTable, mockWorkflowTable)

  val statusService = new StatusService()

}

class TestSamAuthProvider(samConfig: SamConfig) extends SamAuthProvider(samConfig) {
  override lazy val samClient = new MockSamSwaggerClient()
}