package org.riskala.model.eventSourcing.projections

trait Projection[State, Event] {
  def Update: (State, Event) => State
  def Project: (State, Seq[Event]) => State = (initState, events) => events.foldLeft(initState)(Update)
}
