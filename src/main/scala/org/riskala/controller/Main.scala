package org.riskala.controller

import scala.io.StdIn

object Main extends App {

  val route = RouteManager

  println(s"Server online at http://localhost:8080/\nEnter 'exit' to stop...")
  while(StdIn.readLine() != "exit"){
    println("Maybe you want to 'exit'")
  } // let it run until user type exit
  route.exit()

}
