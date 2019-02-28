package org.broadinstitute.workbench.hamm.db

import java.sql.SQLTimeoutException
import org.broadinstitute.workbench.hamm.config.LiquibaseConfig
import scalikejdbc.config.DBs


import scala.concurrent.ExecutionContext

import com.google.common.base.Throwables
import com.typesafe.config.Config
import liquibase.database.jvm.JdbcConnection
import liquibase.{Contexts, Liquibase}
import liquibase.resource.{ClassLoaderResourceAccessor, ResourceAccessor}
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.workbench.hamm.HammLogger
import sun.security.provider.certpath.SunCertPathBuilderException
import scalikejdbc._

object DbReference extends HammLogger {

  def initWithLiquibase(liquibaseConfig: LiquibaseConfig, changelogParameters: Map[String, AnyRef] = Map.empty): Unit = {
    DBs.setupAll()
    val dbConnection = DB.connect()
    try {
      val liquibaseConnection = new JdbcConnection(dbConnection.conn)
      val resourceAccessor: ResourceAccessor = new ClassLoaderResourceAccessor()
      val liquibase = new Liquibase(liquibaseConfig.changelog, resourceAccessor, liquibaseConnection)

      changelogParameters.foreach { case (key, value) => liquibase.setChangeLogParameter(key, value) }
      liquibase.update(new Contexts())
    } catch {
      case e: SQLTimeoutException =>
        val isCertProblem = Throwables.getRootCause(e).isInstanceOf[SunCertPathBuilderException]
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

  def init(config: Config)(implicit executionContext: ExecutionContext): DbReference = {
    val liquibaseConfig = config.as[LiquibaseConfig]("liquibase")

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
}

final case class DbUser(asString: String) extends AnyVal
final case class DbPassword(asString: String) extends AnyVal
final case class SqlConfig(user: DbUser, password: DbPassword, port: Int)