package org.riskala.model.eventSourcing.projections

trait Projection[State, Event] {

  def Init: State

  def Update: (State, Event) => State

  def Project(events: Seq[Event]) : State = events.foldLeft(Init)(Update)
  
  //def Project: (State, Seq[Event]) => State = (initState, events) => events.foldLeft(initState)(Update)
  def Project(snapshot: State, events: Seq[Event]) : State = events.foldLeft(snapshot)(Update)
}
