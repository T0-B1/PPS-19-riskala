package org.riskala.model.game

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.riskala.model.ModelMessages.GameMessage

object GameManager {
  def apply(): Behavior[GameMessage] = Behaviors.ignore[GameMessage]
}
