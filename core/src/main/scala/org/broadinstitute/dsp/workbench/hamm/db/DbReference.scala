package org.broadinstitute.dsp.workbench.hamm.db

import java.sql.SQLTimeoutException

import cats.effect.{Resource, Sync}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, ResourceAccessor}
import liquibase.{Contexts, Liquibase}
import org.broadinstitute.dsp.workbench.hamm.HammLogger
import scalikejdbc._
import scalikejdbc.config.DBs
import sun.security.provider.certpath.SunCertPathBuilderException

import scala.concurrent.ExecutionContext

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

  private[hamm] def init(liquibaseConfig: LiquibaseConfig)(implicit executionContext: ExecutionContext): DbReference = {
    DBs.setupAll()
    if (liquibaseConfig.initWithLiquibase)
      initWithLiquibase(liquibaseConfig)

    DbReference()
  }

  def resource[F[_]: Sync](liquibaseConfig: LiquibaseConfig)(implicit executionContext: ExecutionContext): Resource[F, DbReference] = Resource.make(
    Sync[F].delay(init(liquibaseConfig))
  )(_ => Sync[F].delay(DBs.closeAll()))
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

final case class LiquibaseConfig(changelog: String, initWithLiquibase: Boolean)
