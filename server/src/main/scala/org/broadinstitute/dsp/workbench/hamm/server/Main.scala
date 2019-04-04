package org.broadinstitute.dsp.workbench.hamm
package server

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import fs2.Stream
import org.broadinstitute.dsp.workbench.hamm.config.{GoogleConfig, SamConfig}
import org.broadinstitute.dsp.workbench.hamm.config.config.{GoogleConfigReader, SamConfigReader}
import org.broadinstitute.dsp.workbench.hamm.db.{DbReference, JobTable, WorkflowTable}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import net.ceedubs.ficus.Ficus._
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamAuthProvider

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp with HammLogger {
  override def run(args: List[String]): IO[ExitCode] =  {
    val config = ConfigFactory.parseResources("application.conf").withFallback(ConfigFactory.load())
    val googleConfig =  config.as[GoogleConfig]("google")
    val samConfig =  config.as[SamConfig]("sam")

    val app: Stream[IO, Unit] = for {
      httpClient          <- BlazeClientBuilder[IO](global).stream
      samAuthProvider     = SamAuthProvider(samConfig)
      dbRef <- Stream.eval(IO(DbReference.init(config)))
      hammRoutes          = new HammRoutes(
        samAuthProvider,
        CostService[IO](samAuthProvider, dbRef, JobTable, WorkflowTable),
        StatusService[IO],
        VersionService[IO])
      routes              = hammRoutes.routes
      server              <- BlazeServerBuilder[IO].bindHttp(8080, "0.0.0.0").withHttpApp(routes).serve
    } yield ()

    app.handleErrorWith(error => Stream.emit(logger.error(error)("Failed to start server")))
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
