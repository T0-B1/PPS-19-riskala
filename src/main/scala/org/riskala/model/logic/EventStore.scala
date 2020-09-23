package org.riskala.model.logic

import org.riskala.model.logic.EventStore.Behavior

object EventStore {
  type Behavior[E] = Seq[E]
}

/**
 * An event store.
 * Events can only be added to the store, following a behavior
 *
 * @param events The content of the store
 * @tparam E The type of events in the store
 */
case class EventStore[E](events: Seq[E] = Seq.empty) {

  /**
   * A set of actions are performed and appended to the store
   *
   * @param behavior The actions to perform
   * @return The new event store
   */
  def perform(behavior: Behavior[E]) : EventStore[E] = {
    EventStore(events ++ behavior)
  }

}
