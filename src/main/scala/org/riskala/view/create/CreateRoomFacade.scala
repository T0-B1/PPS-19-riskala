package org.riskala.view.create

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class CreateRoomFacade extends js.Object {
  def notifyCreateError(error: String): Unit = js.native
  def goToRoom(roomInfo: String): Unit = js.native
}
