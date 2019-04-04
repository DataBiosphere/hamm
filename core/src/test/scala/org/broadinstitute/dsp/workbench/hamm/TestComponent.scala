package org.broadinstitute.dsp.workbench.hamm

import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import org.broadinstitute.dsp.workbench.hamm.db._
import org.scalatest.Matchers

import scala.concurrent.ExecutionContext


trait TestComponent extends Matchers {
  implicit val executionContext: ExecutionContext = TestExecutionContext.testExecutionContext
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)

  val dbRef =  DbSingleton.ref

  val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())
}

