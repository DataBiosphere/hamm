package org.broadinstitute.workbench.ccm

import minitest.SimpleTestSuite
import minitest.laws.Checkers
import org.scalacheck.Test
import org.scalacheck.Test.Parameters

trait CcmTestSuite extends SimpleTestSuite with Checkers{
  override def checkConfig: Parameters = Test.Parameters.default.withMinSuccessfulTests(3)
}