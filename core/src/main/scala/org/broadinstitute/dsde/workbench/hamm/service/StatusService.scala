package org.broadinstitute.dsde.workbench.hamm.service


class StatusService  {

  // ToDo: Add checks for services we depend on
  def status(): StatusResponse =  {
    StatusResponse()
  }
}

final case class StatusResponse()
