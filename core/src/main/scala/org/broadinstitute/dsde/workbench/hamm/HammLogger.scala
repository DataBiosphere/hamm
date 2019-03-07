package org.broadinstitute.dsde.workbench.hamm

import org.log4s._


trait HammLogger {
  implicit val logger = getLogger

}
