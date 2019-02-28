package org.broadinstitute.workbench.hamm.db


import org.broadinstitute.workbench.hamm.Generators._
import org.broadinstitute.workbench.hamm.HammTestSuite
import scalikejdbc.DB
import scalikejdbc.config.DBs

object JobCostDAOTest extends HammTestSuite {
  val transactor = DummyDbTransactor.transactor()
  val jobCostDAO = JobCostDAO

  test("sqllike") {
    check1 {
      DBs.setupAll()

      (jobCost: JobCost) =>

      println(jobCost)

      DB.autoCommit { implicit session =>
        JobCostDAO.insertCallCostSql(jobCost)
      }
      val result = DB.readOnly { implicit session =>
        JobCostDAO.getCallCostSql(jobCost.uniqueKey)
      }

      println(result)
      true

    }
  }
}