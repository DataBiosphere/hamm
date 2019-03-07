package org.broadinstitute.workbench.hamm.config

import org.http4s.Uri

case class DatabaseConfig(url: Uri, username: String, password: String)
