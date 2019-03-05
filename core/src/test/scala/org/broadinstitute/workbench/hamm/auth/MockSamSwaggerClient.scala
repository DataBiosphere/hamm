package org.broadinstitute.workbench.hamm.auth

import org.http4s.Uri

class MockSamSwaggerClient extends SamSwaggerClient(Uri.unsafeFromString("fake/path")) {



}
