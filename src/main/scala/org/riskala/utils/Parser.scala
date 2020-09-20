package org.riskala.utils

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import org.riskala.view.messages.WrappedMessage

object Parser {

  case class TypedMessage(classType: Class[_], payload: String)

  def wrap[T](className: String, payload: T, encoder: EncodeJson[T]): String = {
    WrappedMessage(className,payload.asJson(encoder).pretty(spaces2)).asJson.pretty(spaces2)
  }

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
