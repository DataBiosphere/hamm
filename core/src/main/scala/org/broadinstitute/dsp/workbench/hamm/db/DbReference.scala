package org.broadinstitute.dsp.workbench.hamm.db

import java.sql.SQLTimeoutException
import org.broadinstitute.dsp.workbench.hamm.config.config._
import org.broadinstitute.dsp.workbench.hamm.config._
import scalikejdbc.config.DBs
import scala.concurrent.ExecutionContext
import com.typesafe.config.Config
import liquibase.database.jvm.JdbcConnection
import liquibase.{Contexts, Liquibase}
import liquibase.resource.{ClassLoaderResourceAccessor, ResourceAccessor}
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import sun.security.provider.certpath.SunCertPathBuilderException
import scalikejdbc._

object DbReference extends HammLogger {

  private def initWithLiquibase(liquibaseConfig: LiquibaseConfig, changelogParameters: Map[String, AnyRef] = Map.empty): Unit = {
    val dbConnection = DB.connect()
    try {
      val liquibaseConnection = new JdbcConnection(dbConnection.conn)
      val resourceAccessor: ResourceAccessor = new ClassLoaderResourceAccessor()
      val liquibase = new Liquibase(liquibaseConfig.changelog, resourceAccessor, liquibaseConnection)

      changelogParameters.foreach { case (key, value) => liquibase.setChangeLogParameter(key, value) }
      liquibase.update(new Contexts())
    } catch {
      case e: SQLTimeoutException =>
        val isCertProblem = org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(e).isInstanceOf[SunCertPathBuilderException]

        if (isCertProblem) {
          val k = "javax.net.ssl.keyStore"
          if (System.getProperty(k) == null) {
            logger.warn("************")
            logger.warn(s"The system property '${k}' is null. This is likely the cause of the database connection failure.")
            logger.warn("************")
          }
        }
        throw e
    } finally {
      dbConnection.close()
    }
  }

  def dbSetUpAll: Unit = DBs.setupAll()

  def init(liquibaseConfig: LiquibaseConfig)(implicit executionContext: ExecutionContext): DbReference = {
    dbSetUpAll
    if (liquibaseConfig.initWithLiquibase)
      initWithLiquibase(liquibaseConfig)

    DbReference()
  }


}

case class DbReference()(implicit val executionContext: ExecutionContext) {

  def inReadOnlyTransaction[A](f: DBSession => A): A = {
    DB.readOnly[A] { implicit session =>
      f(session)
    }
  }

  def inLocalTransaction[A](f: DBSession => A): A = {
    DB.localTx[A] { implicit session =>
      f(session)
    }
  }
}