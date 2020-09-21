package org.riskala.model.eventsourcing

trait Projection[State, Event] {
  def update: (State, Event) => State
  def project: (State, Seq[Event]) => State = (state, events) => events.foldLeft(state)(update)
}

case class SnapshotGenerator() extends Projection[GameSnapshot, Event]{
  override def update: (GameSnapshot, Event) => GameSnapshot = (game, event) => event.happen(game)
}
