package org.broadinstitute.workbench.hamm.db

import cats.effect.IO
import org.broadinstitute.workbench.hamm.Generators._
import org.broadinstitute.workbench.hamm.HammTestSuite
import scalikejdbc.DB
import scalikejdbc.config.DBs

object JobCostDAOTest extends HammTestSuite {
  val transactor = DummyDbTransactor.transactor()
  val jobCostDAO = JobCostDAO[IO](transactor)

//  test("JobCostDAO should be able to insert WorkflowCostDB object and retrieve successfully"){
//      check1 {
//        (jobCost: JobCost) =>
//          val res = for{
//            _ <- jobCostDAO.createTable
//            _ <- jobCostDAO.insert(jobCost)
//            retrievedObject <- jobCostDAO.getJobCost(jobCost.uniqueKey)
//          } yield {
//            assertEquals(retrievedObject, jobCost)
//          }
//
//          res.unsafeRunSync()
//          true
//      }
//    }
//
  test("sqllike") {
    check1 {
      DBs.setupAll()

      (jobCost: JobCost) =>

      println(jobCost)

      DB.autoCommit { implicit session =>
        JobCostDAO.insertCallCostSql2(jobCost)
      }
      val result = DB.readOnly { implicit session =>
        JobCostDAO.getCallCostSql2(jobCost.uniqueKey)
      }

      println(result)
      true


    }
  }
}