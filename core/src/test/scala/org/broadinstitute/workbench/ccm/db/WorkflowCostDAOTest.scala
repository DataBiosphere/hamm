package org.broadinstitute.workbench.ccm
package db

import cats.effect.IO
import org.broadinstitute.workbench.ccm.Generators._

object WorkflowCostDAOSpec extends CcmTestSuite {
  val transactor = DummyDbTransactor.transactor()
  val workflowCostDAO = WorkflowCostDAO[IO](transactor)

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
            retrievedObject == WorkflowCost(workflowDb.workflowId, workflowDb.cost)
          }

          res.unsafeRunSync()
          true
      }
    }
}