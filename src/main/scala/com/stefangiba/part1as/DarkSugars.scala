package com.stefangiba.part1as

import scala.util.Try
import scala.annotation.targetName

object DarkSugars {

  // 1 - sugar for methods with one argument
  def singleArgMethod(arg: Int): Int = arg + 1

  val methodCall = singleArgMethod({
    // long code
    42
  })

  val methodCall_v2 = singleArgMethod {
    // long code
    42
  }

  // example: Try, Future
  val tryInstance = Try {
    throw new RuntimeException
  }

  // with HOFs
  val incrementedList = List(1, 2, 3).map { x =>
    // code block
    x + 1
  }

  // 2 - single abstract method pattern (since Scala 2.12)
  trait Action {
    // can have other implemented fields / methods
    def act(x: Int): Int
  }
  val action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val anotherAction: Action =
    (x: Int) => x + 1 // new Action {def act(x: Int) = x + 1}

  // example: Runnable
  val thread = new Thread(new Runnable {
    override def run(): Unit = println("Hi, Scala, from another thread")
  })
  val sweeterThread = new Thread(() => println("Hi Scala!"))

  // 3 - methods ending in a `:` are RIGHT-ASSOCIATIVE
  val list          = List(1, 2, 3)
  val prependedList = 0 :: list
  val thing         = list.::(0)
  val bigList       = 0 :: 1 :: 2 :: List(3, 4) // List(3, 4).::(2).::(1).::(0)

  class MyStream[T] {
    infix def -->:(value: T): MyStream[T] = this // impl not important
  }

  val stream = 1 -->: 2 -->: 3 -->: 4 -->: new MyStream[Int]

  // 4 - multi-word identifiers
  class Talker(name: String) {
    infix def `and then said`(gossip: String): Unit = println(
      s"$name said $gossip"
    )
  }

  val daniel           = new Talker("Daniel")
  val danielsStatement = daniel `and then said` "I love scala"

  // example: HTTP Libraries
  object `Content-Type` {
    val `application/json` = "application/JSON"
  }

  // 5 - infix types
  @targetName("Arrow") // more readable bytecode + Java interop
  infix class -->[A, B]
  val compositeType: Int --> String = new -->[Int, String]

  // 6 - update()
  val array = Array(1, 2, 3, 4)
  array.update(2, 45)
  array(2) = 45 // same

  // 7 - mutable fields
  class Mutable {
    private var internalMember: Int = 0

    def member = internalMember // "getter"
    def member_=(value: Int): Unit =
      internalMember = value // "setter"
  }

  val mutableContainer = new Mutable
  mutableContainer.member = 42

  // 8 - variable arguments (varargs)
  def methodWithVarArgs(args: Int*) = {
    // return the number of arguments supplied
    args.length
  }

  val callWithZeroArgs = methodWithVarArgs()
  val callWithOneArg   = methodWithVarArgs(78)
  val callWithTwoArgs  = methodWithVarArgs(12, 34)

  val collection          = List(1, 2, 3, 4)
  val callWithDynamicArgs = methodWithVarArgs(collection*)

  def main(args: Array[String]): Unit = {}
}
