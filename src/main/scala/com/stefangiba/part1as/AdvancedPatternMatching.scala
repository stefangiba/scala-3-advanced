package com.stefangiba.part1as

import scala.annotation.tailrec

object AdvancedPatternMatching {

  /*
  PM:
    - constants
    - objects
    - wildcards
    - variables
    - infix patterns
    - lists
    - case classes
   */

  class Person(val name: String, val age: Int)
  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      if (age < 21) Some("minor")
      else Some("legally allowed to drink")
  }

  val daniel = new Person("Daniel", 102)
  val danielPM = daniel match { // Person.unapply(daniel) => Option((n, a))
    case Person(n, a) => s"Hi there, I'm $n"
  }
  val danielsLegalStatus = daniel.age match {
    case Person(status) => s"Daniel's legal drinking status is: $status."
  }

  // boolean patterns
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 43
  val mathProperty = n match {
    case even()        => "an even number"
    case singleDigit() => "a one digit number"
    case _             => "no special property"
  }

  // infix patterns
  infix case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescriptionEither = either match {
    case number Or string => s"$number is written as $string"
  }

  val list = List(1, 2, 3)
  val listPM = list match {
    case 1 :: rest => "a list starting with 1"
    case _         => "some uninteresting list"
  }

  // decomposing sequences
  val vararg = list match {
    case List(1, _*) => "list starting with 1"
    case _           => "some other list"
  }

  abstract class MyList[A] {
    def head: A         = throw new NoSuchElementException
    def tail: MyList[A] = throw new NoSuchElementException
  }

  case class Empty[A]() extends MyList[A]
  case class Cons[A](override val head: A, override val tail: MyList[A])
      extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      @tailrec
      def go(remainder: MyList[A], acc: Seq[A]): Seq[A] =
        if (remainder == Empty()) acc
        else go(remainder.tail, remainder.head +: acc)

      Some(go(list, Seq.empty).reverse)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty())))
  val varargCustom = myList match {
    case MyList(1, 2, _*) => "list starting with 1"
    case _                => "some other list"
  }

  // custom return type for unapply
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false
      override def get: String      = person.name
    }
  }

  val weirdPersonPM = daniel match {
    case PersonWrapper(name) => s"Hi, my name is $name"
  }

  def main(args: Array[String]): Unit = {
    println(danielPM)
    println(danielsLegalStatus)
    println(mathProperty)
  }
}
