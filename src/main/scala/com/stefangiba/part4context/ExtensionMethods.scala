package com.stefangiba.part4context

import scala.annotation.tailrec

object ExtensionMethods {
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name, nice to meet you!"
  }

  extension (string: String) {
    def greetAsPerson: String = Person(string).greet
  }

  val stefanGreeting = "Stefan".greetAsPerson

  // generic extension methods
  extension [A](list: List[A]) {
    def ends: (A, A) = (list.head, list.last)
  }

  val list      = List(1, 2, 3, 4)
  val firstLast = list.ends

  // reason: make APIs very expressive
  // reason 2: enhance CERTAIN types with new capabilities
  // => super powerful code
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  extension [A](list: List[A])
    def combineAll(using combinator: Semigroup[A]): A =
      list.reduce(combinator.combine)

  given intCombinator: Semigroup[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val firstSum    = list.combineAll
  val someStrings = List("I", "love", "Scala")
  // val stringsSum  = someStrings.combineAll // does not compile - no given Combinator[String] in scope

  // grouping extensions
  object GroupedExtensions {
    extension [A](list: List[A]) {
      def ends: (A, A) = (list.head, list.last)
      def combineAll(using combinator: Semigroup[A]): A =
        list.reduce(combinator.combine)
    }
  }

  // call extension methods directly
  val firstLast_v2 = ends(list) // same as "list.ends"

  /*
    Exercises:
      1. Add an `isPrime` method on the Int type
      2. Add extensions to Tree:
        - map(f: A => B): Tree[B]
        - forall(predicate: A => Boolean): Boolean
        - sum => sum of all elements of the tree
   */
  // "library code" = cannot change
  sealed abstract class Tree[A]
  case class Leaf[A](value: A)                        extends Tree[A]
  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  // 1
  extension (number: Int) {
    def isPrime: Boolean = {
      assert(number > 0)

      val sqrt = Math.sqrt(number).toInt

      @tailrec
      def aux(divisor: Int): Boolean =
        if (divisor > sqrt) true
        else if (number % divisor == 0) false
        else aux(divisor + 1)

      if (number == 1 || number == 2) false else aux(2)
    }
  }

  // 2
  extension [A](tree: Tree[A]) {
    def map[B](f: A => B): Tree[B] = tree match
      case Leaf(value)         => Leaf(f(value))
      case Branch(left, right) => Branch(left.map(f), right.map(f))

    def forall(predicate: A => Boolean): Boolean = tree match
      case Leaf(value) => predicate(value)
      case Branch(left, right) =>
        left.forall(predicate) && right.forall(predicate)

    def combineAll(using combinator: Semigroup[A]): A = tree match
      case Leaf(value) => value
      case Branch(left, right) =>
        combinator.combine(left.combineAll, right.combineAll)

  }

  extension (tree: Tree[Int]) {
    def sum: Int = tree match
      case Leaf(value)         => value
      case Branch(left, right) => left.sum + right.sum

  }

  def main(args: Array[String]): Unit = {
    println(stefanGreeting)
    println(2003.isPrime)

    val tree: Tree[Int] = Branch(Branch(Leaf(3), Leaf(1)), Leaf(10))
    println(tree.map(_ + 1))
    println(tree.forall(_ % 2 == 0))
    println(tree.sum)
    println(tree.combineAll)
  }
}
