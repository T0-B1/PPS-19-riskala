package org.riskala.view.messages

import argonaut.Argonaut.casecodec2

final case class WrappedMessage(classType: String, payload: String)
object WrappedMessage {
  implicit def WrappedCodecJson =
    casecodec2(WrappedMessage.apply,WrappedMessage.unapply)("classType","payload")
}
