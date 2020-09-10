package org.riskala.utils

import org.riskala.controller.actors.PlayerMessages.WrappedMessage
import argonaut.Argonaut._
import argonaut.DecodeJson

object Parser {

  case class TypedMessage(classType: Class[_], payload: String)

  //val x: Class[WrappedMessage] = classOf[WrappedMessage]

  def unwrap(message: String): TypedMessage = {
    val parsedMsg: WrappedMessage = retrieveWrapped(message).get
    val classType = Class.forName(parsedMsg.classType)
    TypedMessage(classType, parsedMsg.payload)
  }

  def retrieveWrapped(wrappedJson: String): Option[WrappedMessage] = {
    wrappedJson.decodeOption[WrappedMessage]
  }

  def retrieveMessage[T](payload: String, decoder: DecodeJson[T]): Option[T] = {
    payload.decodeOption[T](decoder)
  }
}
