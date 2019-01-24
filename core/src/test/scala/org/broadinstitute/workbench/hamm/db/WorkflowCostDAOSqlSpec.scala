package org.broadinstitute.workbench.hamm.db

import cats.effect.IO
import doobie.implicits._
import doobie.specs2._
import org.broadinstitute.workbench.hamm.Generators._
import org.broadinstitute.workbench.hamm.db.WorkflowCostDAO._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

object WorkflowCostDAOSqlSpec extends Specification with IOChecker {
  implicit val cs = IO.contextShift(ExecutionContext.global)


  val workflowDb = genWorkflowDb.sample.get

  check(createSql)
  val res = createSql.run.transact[IO](transactor).unsafeRunSync()
  check(insertWorkflowSql(workflowDb))
  check(getWorkflowCostSql(workflowDb.workflowId))

  override def transactor: doobie.Transactor[IO] = DummyDbTransactor.transactor()
}