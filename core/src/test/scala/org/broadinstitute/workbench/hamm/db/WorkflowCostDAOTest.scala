package org.broadinstitute.workbench.hamm
package db

import cats.implicits._
import cats.effect.IO
import org.broadinstitute.workbench.hamm.Generators._
import org.scalacheck.Arbitrary

object WorkflowCostDAOSpec extends HammTestSuite {
  val transactor = DummyDbTransactor.transactor()
  val workflowCostDAO = WorkflowCostDAO[IO](transactor)

  test("WorkflowCostDAO should be able to insert WorkflowCostDB object and retrieve successfully") {
    implicit val arbWorkflowDb = Arbitrary(genWorkflowDb)
    check1 { (workflowDb: WorkflowDB) =>
      val res = for {
        _ <- workflowCostDAO.createTable
        _ <- workflowCostDAO.insert(workflowDb)
        retrievedObject <- workflowCostDAO.getWorkflowDB(workflowDb.workflowId)
      } yield {
        assertEquals(retrievedObject, workflowDb)
      }

      res.unsafeRunSync()
      true
    }
  }

  test("WorkflowCostDAO should be able to retrieve cost for a given label successfully") {
    implicit val arbWorkflow: Arbitrary[List[WorkflowDB]] = Arbitrary(genListOfWorkflowDBWithSameLabel)
    checkWithNoShrink1 { (workflows: List[WorkflowDB]) =>
      val res = for {
        _ <- workflowCostDAO.createTable
        _ <- workflows.parTraverse(w => workflowCostDAO.insert(w))
        label = workflows(0).label.head
        cost <- workflowCostDAO.getWorkflowCostWithLabel(Label(label._1, label._2))
      } yield {
        assert(cost - workflows.map(_.cost).sum < 0.001) //since it's double, once it's persisted into database, rounding happens; hence there's very minor different between the 2 numbers
      }

      res.unsafeRunSync()
      true
    }
  }
}
