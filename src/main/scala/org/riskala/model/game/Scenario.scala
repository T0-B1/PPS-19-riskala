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

  val bridges: Set[Bridge] = Set(Bridge("Abruzzo", "Molise", false),
                                  Bridge("Abruzzo", "Lazio", false),
                                  Bridge("Abruzzo", "Marche", false),
                                  Bridge("Basilicata", "Calabria", false),
                                  Bridge("Basilicata", "Puglia", false),
                                  Bridge("Basilicata", "Campania", false),
                                  Bridge("Calabria", "Sicilia", false),
                                  Bridge("Campania", "Lazio", false),
                                  Bridge("Campania", "Molise", false),
                                  Bridge("Campania", "Puglia", false),
                                  Bridge("Emilia-Romagna", "Marche", false),
                                  Bridge("Emilia-Romagna", "Toscana", false),
                                  Bridge("Emilia-Romagna", "Liguria", false),
                                  Bridge("Emilia-Romagna", "Piemonte", false),
                                  Bridge("Emilia-Romagna", "Lombardia", false),
                                  Bridge("Emilia-Romagna", "Veneto", false),
                                  Bridge("Friuli-Venezia Giulia", "Veneto", false),
                                  Bridge("Lazio", "Marche", false),
                                  Bridge("Lazio", "Umbria", false),
                                  Bridge("Lazio", "Toscana", false),
                                  Bridge("Lazio", "Molise", false),
                                  Bridge("Liguria", "Toscana", false),
                                  Bridge("Liguria", "Piemonte", false),
                                  Bridge("Lombardia", "Piemonte", false),
                                  Bridge("Lombardia", "Trentino-Alto Adige", false),
                                  Bridge("Lombardia", "Veneto", false),
                                  Bridge("Marche", "Toscana", false),
                                  Bridge("Marche", "Umbria", false),
                                  Bridge("Molise", "Puglia", false),
                                  Bridge("Piemonte", "Valle d'Aosta", false),
                                  Bridge("Sardegna", "Sicilia", false),
                                  Bridge("Sardegna", "Lazio", false),
                                  Bridge("Sardegna", "Toscana", false),
                                  Bridge("Toscana", "Umbria", false),
                                  Bridge("Trentino-Alto Adige", "Veneto", false)
                                )

  val scenario = MapImpl(name, regions, states, bridges)

}
