package org.broadinstitute.workbench.hamm.auth

import org.http4s.Uri

trait SamProvider {
  val samServer: Uri

  protected lazy val samClient = new SamSwaggerClient(samServer)

}
