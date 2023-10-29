package com.stefangiba.part5typesystem

object AdvancedInheritance {
  // 1 - composite types can be used on their own
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Stream[T] {
    def foreach(f: T => Unit): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  // class MyDataStream extends Writer[String] with Stream[String] with Closeable { ... }

  def processStream[T](
      stream: Stream[T] with Writer[T] with Closeable
  ): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // 2 - diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal {
    override def name: String = "Lion"
  }

  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }

  class Liger extends Lion with Tiger

  def demoLiger(): Unit = {
    val liger = new Liger
    println(liger.name)
  }

  /*
  Pseudo-definition:
    class Liger extends Animal
    with { override def name: String = "Lion" }
    with { override def name: String = "Tiger" }

    Last override always gets picked
   */

  // 3 - the super problem
  trait Cold { // cold colors
    def print: Unit = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  def demoColorInheritance(): Unit = {
    val white = new White
    white.print
  }

  /*
    Expected result:
      - white
      - red

    Actual result:
      - white
      - blue
      - green
      - cold
    NO RED!!!

    This is happening because of type linearization

    Cold = AnyRef with <Cold>
    Green = Cold with <Green> = AnyRef with <Cold> with <Green>
    Blue = Cold with <Blue> = AnyRef with <Cold> with <Blue>
    Red = AnyRef with <Red>
    White = Red with <Green> with <Blue> with <White>
          = AnyRef with <Red>
            with (AnyRef with <Cold> with <Green>)
            with (AnyRef with <Cold> with <Blue>)
            with <White>
          = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
   */

  def main(args: Array[String]): Unit = {
    demoLiger()
    demoColorInheritance()
  }
}
