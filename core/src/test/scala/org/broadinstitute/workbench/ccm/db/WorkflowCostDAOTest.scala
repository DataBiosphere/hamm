package org.broadinstitute.workbench.ccm
package db

import cats.Eq
import cats.effect.IO
import cats.implicits._
import org.broadinstitute.workbench.ccm.Generators._

object WorkflowCostDAOSpec extends CcmTestSuite {
  val transactor = new SqlConnection[IO](DBConfig.dbConfig).dbTransactor
  val workflowCostDAO = WorkflowCostDAO[IO](transactor)

  implicit val eq: Eq[WorkflowDB] = Eq.instance{
    (x, y) =>
      x.workflowId == y.workflowId && x.endTime == y.endTime && x.label == y.label && x.cost == y.cost
  }
  test("WorkflowCostDAO should be able to insert WorkflowCostDB object and retrieve successfully"){
      check1 {
        (workflowDb: WorkflowDB) =>
          val res = for{
            createTableResult <- workflowCostDAO.createTable
            insertResult <- workflowCostDAO.insert(workflowDb)
            retrievedObject <- workflowCostDAO.getWorkflowDB(workflowDb.workflowId)
          } yield {
            createTableResult == 0
            insertResult == 0
            retrievedObject === workflowDb
          }

          res.unsafeRunSync()
          true
      }
    }
}