package org.broadinstitute.workbench.ccm
package automation

import cats.effect.{IO, Resource}
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import cats.implicits._

object TestDependencies {
  val managedChannelResource: Resource[IO, ManagedChannel] = for {
    config <- Resource.liftF(AutomationTestConfig.config)
    mc <- ManagedChannelBuilder
            .forAddress(config.host, config.grpcPort)
            .usePlaintext()
            .resource[IO]
  } yield mc
}