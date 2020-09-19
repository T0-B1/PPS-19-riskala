package org.riskala.model.eventSourcing

import org.riskala.model.eventSourcing.EventStore.Behavior

object EventStore {
  type Behavior[Ev] = Seq[Ev] => Seq[Ev]
}

case class EventStore[Ev](events: Seq[Ev]) {

  def apply(): EventStore[Ev] = EventStore(Seq.empty)

  def append(newEvents: Seq[Ev]) : EventStore[Ev] = {
    EventStore(events ++ newEvents)
  }

  def perform(behavior: Behavior[Ev]) : EventStore[Ev] = {
    EventStore(events ++ behavior(events))
  }

}
