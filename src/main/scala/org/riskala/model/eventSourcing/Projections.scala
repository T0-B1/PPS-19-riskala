package org.riskala.model.eventSourcing

trait Projection[State, Event] {
  def Init: State
  def Update: (State, Event) => State
  def Project: Seq[Event] => State = events => events.foldLeft(Init)(Update)
}
