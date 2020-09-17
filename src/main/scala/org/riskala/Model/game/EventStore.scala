package org.riskala.model.game

case class EventStore[Ev](events: Seq[Ev]) {

  type Behavior[Ev] = Seq[Ev] => Seq[Ev]

  def apply(): EventStore[Ev] = EventStore(Seq.empty)

  def append(newEvents: Seq[Ev]) : EventStore[Ev] = {
    EventStore(events ++ newEvents)
  }

  def perform(behavior: Behavior[Ev]) : EventStore[Ev] = {
    EventStore(events ++ behavior(events))
  }

}