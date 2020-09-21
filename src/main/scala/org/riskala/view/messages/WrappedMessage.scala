package org.riskala.view.messages

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson

final case class WrappedMessage(classType: String, payload: String)
object WrappedMessage {
  implicit def WrappedCodecJson: CodecJson[WrappedMessage] =
    casecodec2(WrappedMessage.apply,WrappedMessage.unapply)("classType","payload")
}
