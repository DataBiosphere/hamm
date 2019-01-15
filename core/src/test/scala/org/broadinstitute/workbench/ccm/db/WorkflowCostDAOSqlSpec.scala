package org.broadinstitute.workbench.ccm.db

import cats.effect.IO
import doobie.implicits._
import doobie.specs2._
import org.broadinstitute.workbench.ccm.Generators._
import org.broadinstitute.workbench.ccm.db.WorkflowCostDAO._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

object WorkflowCostDAOSqlSpec extends Specification with IOChecker {
  implicit val cs = IO.contextShift(ExecutionContext.global)


  val workflowDb = genWorkflowDb.sample.get

  check(create)
  val res = create.run.transact[IO](transactor).unsafeRunSync()
  check(insertConnIO(workflowDb))
  check(getWorkflowDBConnIO(workflowDb.workflowId))

  override def transactor: doobie.Transactor[IO] = DummyDbTransactor.transactor()
}