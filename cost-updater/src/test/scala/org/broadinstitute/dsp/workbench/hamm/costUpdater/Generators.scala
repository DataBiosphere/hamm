package org.broadinstitute.dsp.workbench.hamm.costUpdater
import org.broadinstitute.dsde.workbench.google2.Generators.{genGcsBlobName, genGcsBucketName}
import org.scalacheck.Arbitrary

object Generators {
  val genNotificationMessage = for {
    bucketName <- genGcsBucketName
    blobName <- genGcsBlobName
  } yield NotificationMessage(BucketAndObject(bucketName, blobName))

  implicit val arbNotificationMessage: Arbitrary[NotificationMessage] = Arbitrary(genNotificationMessage)
}
