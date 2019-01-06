package org.broadinstitute.workbench.ccm

import cats.effect.IO
import minitest.SimpleTestSuite
import minitest.laws.Checkers
import org.scalacheck.Test
import org.scalacheck.Test.Parameters

import scala.concurrent.ExecutionContext.Implicits.global

trait CcmTestSuite extends SimpleTestSuite with Checkers{
  implicit val cs = IO.contextShift(global)
  implicit val timer = IO.timer(global)

  override def checkConfig: Parameters = Test.Parameters.default.withMinSuccessfulTests(3)
}