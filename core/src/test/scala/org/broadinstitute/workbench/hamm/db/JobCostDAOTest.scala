package org.broadinstitute.workbench.hamm.db


import org.broadinstitute.workbench.hamm.Generators._
import org.broadinstitute.workbench.hamm.HammTestSuite
import scalikejdbc.DB
import scalikejdbc.config.DBs

object JobCostDAOTest extends HammTestSuite {
//  val transactor = DummyDbTransactor.transactor()
  val jobCostDAO = JobTableQueries

  test("sqllike") {
    check1 {
      DBs.setupAll()

      (jobCost: Job) =>

      println(jobCost)

      DB.autoCommit { implicit session =>
        JobTableQueries.insertCallSql(jobCost)
      }
      val result = DB.readOnly { implicit session =>
        JobTableQueries.getCallSql(jobCost.uniqueKey)
      }

      println(result)
      true

    }
  }
}