package org.broadinstitute.workbench.hamm.db

import cats.effect.IO
import org.broadinstitute.workbench.hamm.Generators._
import org.broadinstitute.workbench.hamm.HammTestSuite

object JobCostDAOTest extends HammTestSuite {
  val transactor = DummyDbTransactor.transactor()
  val jobCostDAO = JobCostDAO[IO](transactor)

  test("JobCostDAO should be able to insert WorkflowCostDB object and retrieve successfully"){
      check1 {
        (jobCost: JobCost) =>
          val res = for{
            _ <- jobCostDAO.createTable
            _ <- jobCostDAO.insert(jobCost)
            retrievedObject <- jobCostDAO.getJobCost(jobCost.uniqueKey)
          } yield {
            retrievedObject == jobCost
          }

          res.unsafeRunSync()
          true
      }
    }
}