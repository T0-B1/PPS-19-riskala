package org.riskala.model.logic

/**
 * An operation over a set of event for producing a state
 *
 * @tparam State Type of state to produce
 * @tparam Event Type of events to evaluate
 */
trait Projection[State, Event] {

  /**
   * The update function
   *
   * @return A new state after the application of a single event
   */
  def update: (State, Event) => State

  /**
   * Returns a new state which is a fold over all the events given an initial state
   * @return The new state, result of all the events
   */
  def project: (State, Seq[Event]) => State = (state, events) => events.foldLeft(state)(update)
}

/**
 * A projection that generates a snapshot of the current state of the whole game
 */
case class SnapshotGenerator() extends Projection[GameSnapshot, Event]{
  override def update: (GameSnapshot, Event) => GameSnapshot = (game, event) => event.happen(game)
}
