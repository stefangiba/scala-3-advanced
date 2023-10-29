package com.stefangiba.part5typesystem

import java.awt.Component

object SelfTypes {
  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist => // self-type: whoever implemets Singer MUST also implement Instrumentalist
    // ^^ name can be anything, usually called "self"
    // DO NOT confuse this with a lambda

    // rest of your api
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("some guitar solo")
  }

  val ericClapton =
    new Guitarist with Singer { // ok - extending Guitarist <: Instrumentalist
      override def sing(): Unit = ???
    }

  // self-types vs inheritance
  class A
  class B extends A // B "is an " A

  trait T
  trait S { self: T => } // S "requires a" T

  // self-types for DI = "cake pattern"
  abstract class Component {
    // main general API
  }

  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component) // regular DI

  // cake pattern
  trait ComponentLayer1 {
    // API
    def actionLayer1(x: Int): String
  }
  trait ComponentLayer2 { self: ComponentLayer1 =>
    // some other API
    def actionLayer2(x: String): Int
  }
  trait Application { self: ComponentLayer1 with ComponentLayer2 =>
    // your main API
  }

  // example: a photo taking application API in the style of Instagram
  // layer 1 - small components
  trait Picture extends ComponentLayer1
  trait Stats   extends ComponentLayer1

  // layer 2 - compose
  trait ProfilePage extends ComponentLayer2 with Picture
  trait Analytics   extends ComponentLayer2 with Stats

  // layer 3 - application
  trait AnalyticsApp extends Application with Analytics
  // dependencies are specified in lauyers, like baking a cake
  // when you put the pieces together, you can pcik a possible implementation for each layer

  // self-types: preserve the "this" instance
  class SingerWithInnnerClass {
    self => // self-type with no type requirement, self == this
    class Voice {
      def sing(): String = this
        .toString() // this == the voice, use "self" to refer to the outer instance
    }
  }

  // cyclical inheritance does not work
  // class X extends Y
  // class Y extends X

  // cyclical dependencies
  trait X { self: Y => }
  trait Y { self: X => }
  trait Z extends X with Y // all good

  def main(args: Array[String]): Unit = {}
}
