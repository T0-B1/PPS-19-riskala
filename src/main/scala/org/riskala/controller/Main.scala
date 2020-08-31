package org.riskala.controller

import scala.io.StdIn

object Main extends App {

  val route = RouteManager

  println(s"Server online at http://localhost:8080/\nPress enter to stop...")
  StdIn.readLine()
  route.exit()

}
