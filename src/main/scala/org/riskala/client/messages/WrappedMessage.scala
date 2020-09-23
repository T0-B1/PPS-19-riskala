package org.riskala.client.messages

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson

/**
 * A wrapper for a json message indicating the classType of the message contained in the payload #scala.js
 *
 * @param classType The type of the payload
 * @param payload
 */
final case class WrappedMessage(classType: String, payload: String)

object WrappedMessage {
  implicit def WrappedCodecJson: CodecJson[WrappedMessage] =
    casecodec2(WrappedMessage.apply,WrappedMessage.unapply)("classType","payload")
}
