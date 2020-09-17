package org.riskala.view.game

import argonaut.Argonaut.{ToJsonIdentity, nospace}
import org.riskala.utils.Parser
import org.riskala.view.messages.FromClientMessages.ActionMessage
import org.riskala.view.messages.WrappedMessage

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ClientGame")
object ClientGame {

  @JSExport
  def getEmptyMsgWrapped(typeMessage: String): String =  { //per leaveMsg, logoutMsg e endTurnMsg
    WrappedMessage("ReadyMessage", "").asJson.pretty(nospace)
  }
}
