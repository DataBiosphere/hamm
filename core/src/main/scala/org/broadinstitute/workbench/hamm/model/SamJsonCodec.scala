package org.broadinstitute.workbench.hamm.model

import io.circe.Decoder


object SamJsonCodec {
  implicit val SamUserInfoResponseDecoder: Decoder[SamUserInfoResponse] = Decoder.forProduct3("userSubjectId", "userEmail", "enabled")(SamUserInfoResponse)
}
