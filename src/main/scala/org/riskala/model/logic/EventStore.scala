package org.riskala.model.logic

import org.riskala.model.logic.EventStore.Behavior

object EventStore {
  type Behavior[Ev] = Seq[Ev]
}

case class EventStore[Ev](events: Seq[Ev] = Seq.empty) {

  def append(newEvents: Seq[Ev]) : EventStore[Ev] = {
    EventStore(events ++ newEvents)
  }

  def perform(behavior: Behavior[Ev]) : EventStore[Ev] = {
    EventStore(events ++ behavior)
  }

}
