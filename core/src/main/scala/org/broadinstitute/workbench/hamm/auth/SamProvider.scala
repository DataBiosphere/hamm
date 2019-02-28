package org.broadinstitute.workbench.hamm.auth

import org.broadinstitute.workbench.hamm.config.SamConfig

trait SamProvider {
  val config: SamConfig

  protected lazy val samClient = new SamSwaggerClient(config.samUrl)

}
