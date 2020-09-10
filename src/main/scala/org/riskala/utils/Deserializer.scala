package org.riskala.utils

import org.riskala.controller.actors.PlayerMessages.WrappedMessage
import argonaut.Argonaut._
import argonaut.DecodeJson

object Deserializer {

  def retrieveWrapped(wrappedJson: String): Option[WrappedMessage] = {
    wrappedJson.decodeOption[WrappedMessage]
  }

  def retrieveMessage[T](payload: String, decoder: DecodeJson[T]): Option[T] = {
    payload.decodeOption[T](decoder)
  }
}
