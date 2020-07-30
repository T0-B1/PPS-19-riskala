package org.riskala.Model

case class MapImpl(override val name:String,
                   override val regions: List[Region],
                   override val states: List[State],
                   override val bridges: List[Bridge]) extends Map {

  /*override def neighbor(state: State): List[State] = {
    bridges.filter(x => x.state1 == state || x.state2 == state)
      .map(b => if(b.state1==state)b.state2 else b.state1)
  }*/

  override def neighbor(state: State): List[State] = bridges collect {
    case BridgeImpl(s1,s2,_) if s1 == state => s2
    case BridgeImpl(s1,s2,_) if s2 == state => s1
  }

  override def areNeighbor(state1: State, state2: State): Boolean =
    bridges.contains(BridgeImpl(state1,state2,false))
}
