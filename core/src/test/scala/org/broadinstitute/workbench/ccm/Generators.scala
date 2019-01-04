package org.broadinstitute.workbench.ccm

import org.scalacheck.{Arbitrary, Gen}

object Generators {
  val genCpu = Gen.alphaStr.map(Cpu)
  val genBootDiskSizedGb = Gen.posNum[Int].map(BootDiskSizeGb)
}
