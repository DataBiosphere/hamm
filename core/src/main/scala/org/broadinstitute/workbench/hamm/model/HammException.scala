package org.broadinstitute.workbench.hamm.model

case class HammException(status: Int, regrets: String, cause: Option[Throwable]) extends Throwable(regrets)

object HammException {
  def apply(status: Int, message: String): HammException = HammException(status, message, None)
  def apply(status: Int, message: String, cause: Throwable): HammException = HammException(status, message, Option(cause))
}
