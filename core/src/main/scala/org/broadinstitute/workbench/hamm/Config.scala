package org.broadinstitute.workbench.hamm

import cats.implicits._
import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.generic.auto._

object Config  {
  implicit val uriConfigReader = ConfigReader.fromStringTry(s => Uri.fromString(s).toTry)
  val appConfig = pureconfig.loadConfig[AppConfig].leftMap(failures => new RuntimeException(failures.toList.map(_.description).mkString("\n")))
}

final case class Grpc(port: Int)
final case class AppConfig(grpc: Grpc, pricingGoogleUrl: Uri)