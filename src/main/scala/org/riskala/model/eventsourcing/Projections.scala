package org.riskala.model.eventsourcing

trait Projection[State, Event] {
  def Update: (State, Event) => State
  def Project: (State, Seq[Event]) => State = (state, events) => events.foldLeft(state)(Update)
}

case class SnapshotGenerator() extends Projection[GameSnapshot, Event]{
  override def Update: (GameSnapshot, Event) => GameSnapshot = (game, event) => event.happen(game)
}
