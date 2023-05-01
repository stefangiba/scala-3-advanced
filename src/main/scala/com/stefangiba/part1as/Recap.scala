package com.stefangiba.part1as

import scala.annotation.tailrec

object Recap {

  // values, types, expressions
  val condition = false // vals are constants
  val ifExpression =
    if (condition) 42 else 55 // expressions evaluate to a value

  val codeBlock = {
    if (condition) 54
    78
  }

  // types: Int, String, Double, Boolean, Char, ...
  // Unit = () == "void" in other languages
  val theUnit = println("Hello, Scala")

  // functions
  def function(x: Int): Int = x + 1

  // recurstion: stack & tail
  @tailrec
  private def factorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else factorial(n - 1, n * acc)

  val fact10 = factorial(10, 1)

  // Object Oriented Programming
  class Animal
  class Dog extends Animal
  val dog: Animal = new Dog

  trait Carnivore {
    infix def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override infix def eat(a: Animal): Unit = println(
      "I'm a croc, I eat everything."
    )
  }

  // method notation
  val croc = new Crocodile
  croc.eat(dog)
  croc eat dog // "operator" / infix position

  // anonymous classes
  val carnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("I'm a carnivore!")
  }

  // generics
  abstract class LList[A] {
    // type A is known inside the implementation
  }

  // singletons and companions
  object LList // companion object, used for instance-independent ("static") fields / methods

  // case classes
  case class Person(name: String, age: Int)

  // enums
  enum BasicColors {
    case RED, GREEN, BLUE
  }

  // exceptions and try/catch/finally
  def throwSomeException(): Int = throw new RuntimeException()

  val potentialFailure =
    try {
      throwSomeException()
    } catch {
      case e: Exception => "I caught an exception"
    } finally {
      // closing resources, and otherwise important code that must run no matter what
      println("some important logs")
    }

  // functional programming
  val incrementer = new Function1[Int, Int] {
    override def apply(x: Int): Int = x + 1
  }

  val two = incrementer(1)

  // lambdas
  val anonymousIncrementer = (x: Int) => x + 1

  // HOFs => higher order functions
  val incrementedList = List(1, 2, 3).map(anonymousIncrementer)
  // map, flatMap, filter

  // for comprehensions
  val pairs = for {
    number <- List(1, 2, 3)
    char   <- List('a', 'b')
  } yield s"$number-$char"

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples, Sets

  // Option, Try
  val option: Option[Int] = Option(42)

  // Pattern Matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }

  def main(args: Array[String]): Unit = {}
}
