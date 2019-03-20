package org.broadinstitute.dsp.workbench.hamm

import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import fs2._
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsp.workbench.hamm.api.HammRoutes
import org.broadinstitute.dsp.workbench.hamm.config.{GoogleConfig, SamConfig}
import org.broadinstitute.dsp.workbench.hamm.config.config.{GoogleConfigReader, SamConfigReader}
import org.broadinstitute.dsp.workbench.hamm.db.{DbReference, JobTable, WorkflowTable}
import org.broadinstitute.dsp.workbench.hamm.service.{CostService, StatusService}
import org.broadinstitute.dsp.workbench.hamm.auth.SamAuthProvider
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global //use better thread pool

object Main extends IOApp with HammLogger {
  override def run(args: List[String]): IO[ExitCode] =  {
    val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())
    val googleConfig =  config.as[GoogleConfig]("google")
    val samConfig =  config.as[SamConfig]("sam")

    val dbRef = DbReference.init(config)

    val app: Stream[IO, Unit] = for {
      httpClient          <- BlazeClientBuilder[IO](global).stream
      samAuthProvider     = new SamAuthProvider(samConfig)
      jobTable            = new JobTable
      workflowTable       = new WorkflowTable
      costService         = new CostService(samAuthProvider, dbRef, jobTable, workflowTable)
      statusService       = new StatusService
      hammRoutes          = new HammRoutes(samAuthProvider, costService, statusService)
      routes              = hammRoutes.routes
      server              <- BlazeServerBuilder[IO].bindHttp(8080, "localhost").withHttpApp(routes).serve
    } yield ()

    app.handleErrorWith(error => Stream.emit(logger.error(error)("Failed to start server")))
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
