//package org.broadinstitute.workbench.hamm.db
//
//import cats.effect.IO
//import doobie.implicits._
//import doobie.specs2._
//import org.broadinstitute.workbench.hamm.Generators._
//import org.broadinstitute.workbench.hamm.db.WorkflowCostDAO._
//import org.specs2.mutable.Specification
//
//import scala.concurrent.ExecutionContext
//
//object WorkflowCostDAOSqlSpec extends Specification with IOChecker {
//  implicit val cs = IO.contextShift(ExecutionContext.global)
//
//
//  val workflowDb = genNonEmptyLabelWorkflowDb.sample.get
//
//  check(createSql)
//  val res = createSql.run.transact[IO](transactor).unsafeRunSync()
//
//  check(insertWorkflowSql(workflowDb))
//  check(getWorkflowDBSql(workflowDb.workflowId))
//  check(getWorkflowCostSql(workflowDb.workflowId))
//  check(getWorkflowCollectionIdSql(workflowDb.workflowId))
//
//  val oneLabel = workflowDb.label.head
//  check(getWorkflowCostSqlWithLabel(Label(oneLabel._1, oneLabel._2)))
//
//  override def transactor: doobie.Transactor[IO] = DummyDbTransactor.transactor()
//}