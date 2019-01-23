package org.broadinstitute.workbench.ccm.db

import cats.effect.IO
import doobie.implicits._
import doobie.specs2._
import org.broadinstitute.workbench.ccm.Generators._
import org.broadinstitute.workbench.ccm.db.JobCostDAO._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

object JobCostDAOSqlSpec extends Specification with IOChecker {
  implicit val cs = IO.contextShift(ExecutionContext.global)


  val callCost = genCallCost.sample.get

  check(createSql)
  val res = createSql.run.transact[IO](transactor).unsafeRunSync()
  check(insertCallCostSql(callCost))
  val callUniqueKey = CallUniquekey(callCost.workflowId, callCost.callFqn, callCost.attempt)
  check(getCallCostSql(callUniqueKey))

  override def transactor: doobie.Transactor[IO] = DummyDbTransactor.transactor()
}