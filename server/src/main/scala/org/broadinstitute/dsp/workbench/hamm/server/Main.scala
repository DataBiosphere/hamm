package org.broadinstitute.dsp.workbench.hamm
package server

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.Stream
import org.broadinstitute.dsp.workbench.hamm.db.{DbReference, JobTable, WorkflowTable}
import org.broadinstitute.dsp.workbench.hamm.server.auth.SamAuthProvider
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp with HammLogger {
  override def run(args: List[String]): IO[ExitCode] =  {
    val app: Stream[IO, Unit] = for {
      appConfig <- Stream.fromEither[IO](Config.appConfig)
      httpClient          <- BlazeClientBuilder[IO](global).stream
      samAuthProvider     = SamAuthProvider(appConfig.sam)
      dbRef <- Stream.resource(DbReference.resource[IO](appConfig.liquibase))
      hammRoutes          = new HammRoutes(
        samAuthProvider,
        CostService[IO](samAuthProvider, dbRef, JobTable, WorkflowTable),
        StatusService[IO],
        VersionService[IO])
      server              <- BlazeServerBuilder[IO].bindHttp(8080, "0.0.0.0").withHttpApp(hammRoutes.routes).serve
    } yield ()

    app.handleErrorWith(error => Stream.emit(logger.error(error)("Failed to start server")))
      .evalMap(_ => IO.never)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
