package org.broadinstitute.workbench.hamm

import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import fs2._
import org.broadinstitute.workbench.hamm.config.{CromwellConfig, GoogleConfig, SamConfig}
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.workbench.hamm.api.HammRoutes
import org.broadinstitute.workbench.hamm.auth.SamAuthProvider
import org.broadinstitute.workbench.hamm.dao.{GooglePriceListDAO, WorkflowMetadataDAO}
import org.broadinstitute.workbench.hamm.service.{StatusService, WorkflowCostService}
import org.broadinstitute.workbench.hamm.db.DbReference
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global //use better thread pool

object Main extends IOApp with HammLogger {
  override def run(args: List[String]): IO[ExitCode] =  {
    val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())
    val googleConfig =  config.as[GoogleConfig]("google")
    val cromwellConfig =  config.as[CromwellConfig]("cromwell")
    val samConfig =  config.as[SamConfig]("sam")

    val dbRef = DbReference.init(config)

    val app: Stream[IO, Unit] = for {
      //appConfig           <- Stream.fromEither[IO](Config.appConfig)
      httpClient          <- BlazeClientBuilder[IO](global).stream
      pricing             = new GooglePriceListDAO(httpClient, googleConfig)
      metadataDAO         = new WorkflowMetadataDAO(httpClient, cromwellConfig)
      samAuthProvider     = new SamAuthProvider(samConfig)
      workflowCostService = new WorkflowCostService(pricing, metadataDAO, samAuthProvider, dbRef)
      statusService       = new StatusService
      hammRoutes          = new HammRoutes(samAuthProvider, workflowCostService, statusService)
      routes              = hammRoutes.routes
      server              <- BlazeServerBuilder[IO].bindHttp(8080, "localhost").withHttpApp(routes).serve    //.compile.drain.as(ExitCode.Success)  // new WorkflowCostService[IO](pricing, metadataDAO, samAuthProvider)).start
    } yield ()

    app.handleErrorWith(error => Stream.emit(logger.error(error)("Failed to start server")))
//      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
