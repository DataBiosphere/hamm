//package org.broadinstitute.workbench.hamm
//package costUpdater
//
//import cats.implicits._
//import pureconfig.ConfigReader
//import pureconfig.generic.auto._
//
//object Config {
//  val appConfig = pureconfig.loadConfig[AppConfig].leftMap(failures => new RuntimeException(failures.toList.map(_.description).mkString("\n")))
//}
//
//final case class Google(subscriberServiceAccountPath: SubscriberServiceAccountPath)
//final case class SubscriberServiceAccountPath(asStirng: String) extends AnyVal