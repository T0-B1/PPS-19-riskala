package org.riskala.model.game

import org.riskala.model.{Bridge, MapImpl, Region}
import org.riskala.model.State.State

object Scenario extends App{

  val name: String = "Italy"
  val states: Set[State]  = Set("Abruzzo",
    "Basilicata",
    "Calabria",
    "Campania",
    "Emilia-Romagna",
    "Friuli-Venezia Giulia",
    "Lazio",
    "Liguria",
    "Lombardia",
    "Marche",
    "Molise",
    "Piemonte",
    "Puglia",
    "Sardegna",
    "Sicilia",
    "Toscana",
    "Trentino-Alto Adige",
    "Umbria",
    "Valle d'Aosta",
    "Veneto")

  val northernStates: Set[State] = Set("Emilia-Romagna",
                              "Friuli-Venezia Giulia",
                              "Liguria",
                              "Lombardia",
                              "Piemonte",
                              "Trentino-Alto Adige",
                              "Valle d'Aosta",
                              "Veneto")

  val north: Region = Region("North", northernStates, 8)

  val centerStates: Set[State] = Set("Lazio",
                               "Marche",
                               "Toscana",
                               "Umbria")

  val center: Region = Region("Center", centerStates, 4)

  val southernStates: Set[State] = Set("Abruzzo",
                              "Basilicata",
                              "Calabria",
                              "Campania",
                              "Molise",
                              "Puglia")

  val south: Region = Region("South", southernStates, 6)


  val islandStates: Set[State] = Set("Sardegna",
                                "Sicilia")

  val islands: Region = Region("Islands", islandStates, 2)

  val regions: Set[Region] = Set(north, center, south, islands)

  val bridges: Set[Bridge] = Set()

  val scenario = MapImpl(name, regions, states, bridges)

}
