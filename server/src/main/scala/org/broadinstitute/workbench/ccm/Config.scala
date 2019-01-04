package org.broadinstitute.workbench.ccm

import cats.implicits._
import pureconfig.generic.auto._
import org.http4s.Uri
import pureconfig.ConfigReader

object Config {
  final case class Grpc(port: Int)
  final case class AppConfig(grpc: Grpc, pricingGoogleUrl: Uri)

  implicit val uriConfigReader = ConfigReader.fromStringTry(s => Uri.fromString(s).toTry)
  val appConfig = pureconfig.loadConfig[AppConfig].leftMap(failures => new RuntimeException(failures.toList.map(_.description).mkString("\n")))
}
