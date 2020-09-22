package org.riskala.utils

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import org.riskala.view.messages.WrappedMessage

/**
 * #scala.js
 */
object Parser {

  case class TypedMessage(classType: Class[_], payload: String)

  /**
   * Method that generates a JSON-format string from an object
   * @param className Name of the class encoded
   * @param payload The object which will be encoded
   * @param encoder The object that converts payload to JSON
   * */
  def wrap[T](className: String, payload: T, encoder: EncodeJson[T]): String = {
    WrappedMessage(className,payload.asJson(encoder).pretty(spaces2)).asJson.pretty(spaces2)
  }

  /**
   * Method used to retrieve a typed message from a JSON-format string
   * */
  def unwrap(message: String): TypedMessage = {
    val WrappedMessage(classType,payload) = retrieveWrapped(message).get
    TypedMessage(Class.forName(classType), payload)
  }

  /**
   * Method used to retrieve a wrapped message from a JSON-format string
   * */
  def retrieveWrapped(wrappedJson: String): Option[WrappedMessage] = {
    wrappedJson.decodeOption[WrappedMessage]
  }

  /**
   * Method used to retrieve an object from a JSON-format string
   * @param payload The JSON-format string of an object
   * @param decoder The object used to decode the payload
   * */
  def retrieveMessage[T](payload: String, decoder: DecodeJson[T]): Option[T] = {
    payload.decodeOption[T](decoder)
  }
}
