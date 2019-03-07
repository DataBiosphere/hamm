package org.broadinstitute.dsde.workbench.hamm

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.ExecutionContextExecutor

object TestExecutionContext {
  implicit val testExecutionContext = new TestExecutionContext()
}

class TestExecutionContext() extends ExecutionContextExecutor {
  val pool: ExecutorService = Executors.newCachedThreadPool()
  override def execute(runnable: Runnable): Unit = {
    pool.execute(runnable)
  }

  override def reportFailure(cause: Throwable): Unit = {
    cause.printStackTrace()
  }
}
