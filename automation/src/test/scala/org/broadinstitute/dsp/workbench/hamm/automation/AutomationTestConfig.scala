package org.broadinstitute.dsp.workbench.hamm
package automation

import cats.effect.IO
import cats.implicits._
import ciris._
import ciris.cats.effect._

object AutomationTestConfig {
  val config: IO[AutomationTestConfig] = loadConfig(
    propF[IO, Option[String]]("host"),
    propF[IO, Option[Int]]("grpc.automation.port")
  ) { (hostOpt, grpcPortOpt) =>
    val host = hostOpt.getOrElse("127.0.0.1")
    val grpcPort = grpcPortOpt.getOrElse(9999)

    AutomationTestConfig(host, grpcPort)
  }.flatMap(x => IO.fromEither(x.leftMap(_.toException).leftWiden))
}

final case class AutomationTestConfig(host: String, grpcPort: Int)