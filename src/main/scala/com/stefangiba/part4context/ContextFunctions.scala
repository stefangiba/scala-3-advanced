package com.stefangiba.part4context

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object ContextFunctions {
  val list       = List(1, 2, 3, 4)
  val sortedList = list.sorted

  // defs can take using clauses
  def methodWithoutContextArguments(nonContextArg: Int)(
      nonContextArg2: String
  ): String = ???

  def methodWithContextArguments(nonContextArg: Int)(using
      nonContextArg2: String
  ): String = ???

  // eta-expansion
  val functionWithoutContextArguments = methodWithoutContextArguments
  // val func2 = methodWithContextArguments // doesn't work

  // context function
  val functionWithContextArguments: Int => String ?=> String =
    methodWithContextArguments

  /*
    - convert methods with using clauses to function values
    - HOF with function values taking given instances as arguments
   */
  // execution context here
  // val incrementAsync: Int => Future[Int] = x => Future(x + 1) // doesn't work without a given EC in scope

  val incrementAsync: ExecutionContext ?=> Int => Future[Int] = x =>
    Future(x + 1)

  def main(args: Array[String]): Unit = {}
}
