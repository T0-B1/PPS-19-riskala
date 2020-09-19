package org.riskala.model.eventSourcing.projections

trait Projection[State, Event] {
  def Update: (State, Event) => State
  def Project: (State, Seq[Event]) => State = (state, events) => events.foldLeft(state)(Update)
}
