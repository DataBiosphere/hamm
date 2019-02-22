package org.broadinstitute.dsp.workbench.hamm

import cats.effect.IO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import minitest.SimpleTestSuite
import minitest.laws.Checkers
import org.scalacheck.Test.Parameters
import org.scalacheck.util.Pretty
import org.scalacheck.{Arbitrary, Prop, Test}

import scala.concurrent.ExecutionContext.Implicits.global

trait HammTestSuite extends SimpleTestSuite with Checkers{
  implicit val cs = IO.contextShift(global)
  implicit val timer = IO.timer(global)
  override def checkConfig: Parameters = Test.Parameters.default.withMinSuccessfulTests(3)
  implicit val logger = Slf4jLogger.unsafeCreate[IO]

  def checkWithNoShrink1[A1,P](f: A1 => P, config: Parameters = checkConfig)
                  (implicit
                   p: P => Prop,
                   a1: Arbitrary[A1], pp1: A1 => Pretty
                  ): Unit = {

    check((Prop.forAllNoShrink(f)(p, a1, pp1)), config)
  }
}
