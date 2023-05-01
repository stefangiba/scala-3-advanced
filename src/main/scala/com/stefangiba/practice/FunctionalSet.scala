package com.stefangiba.practice

import com.stefangiba.practice

import scala.collection.View.Empty
import scala.annotation.tailrec

sealed abstract class FunctionalSet[A] extends (A => Boolean) {
  // main API
  def contains(element: A): Boolean
  def apply(element: A): Boolean = contains(element)

  infix def +(element: A): FunctionalSet[A]
  infix def ++(anotherSet: FunctionalSet[A]): FunctionalSet[A]

  // "classics"
  def map[B](f: A => B): FunctionalSet[B]
  def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B]
  def filter(predicate: A => Boolean): FunctionalSet[A]
  def foreach(f: A => Unit): Unit

  // utilities
  infix def -(element: A): FunctionalSet[A]
  infix def --(anotherSet: FunctionalSet[A]): FunctionalSet[A]
  infix def &(anotherSet: FunctionalSet[A]): FunctionalSet[A]

  // "negation" == all the elements of type A EXCEPT the elements in this set
  def unary_! : FunctionalSet[A] = new PropertyBasedSet(!contains(_))
}

// example : { x in N | x % 2 == 0 }
class PropertyBasedSet[A](property: A => Boolean) extends FunctionalSet[A] {
  override def contains(element: A): Boolean = property(element)

  override infix def +(element: A): FunctionalSet[A] =
    new PropertyBasedSet(x => x == element | property(x))

  override infix def ++(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    new PropertyBasedSet(x => property(x) || anotherSet(x))

  override def map[B](f: A => B): FunctionalSet[B] = politelyFail()

  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] =
    politelyFail()

  override def filter(predicate: A => Boolean): FunctionalSet[A] =
    new PropertyBasedSet(x => property(x) && predicate(x))

  override def foreach(f: A => Unit): Unit = politelyFail()

  override infix def -(element: A): FunctionalSet[A] = filter(_ != element)

  override infix def --(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    filter(!anotherSet)

  override infix def &(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    filter(anotherSet)

  private def politelyFail() = throw new RuntimeException(
    "I don't know if this set is iterable..."
  )
}

case class Empty[A]() extends FunctionalSet[A] {
  override def contains(element: A): Boolean   = false
  override def +(element: A): FunctionalSet[A] = Cons(element, Empty())
  override def ++(anotherSet: FunctionalSet[A]): FunctionalSet[A] = anotherSet
  override def map[B](f: A => B): FunctionalSet[B]                = Empty()
  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] = Empty()
  override def filter(predicate: A => Boolean): FunctionalSet[A]      = Empty()
  override def foreach(f: A => Unit): Unit                            = ()
  override infix def -(element: A): FunctionalSet[A]                  = Empty()
  override infix def --(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    Empty()
  override infix def &(anotherSet: FunctionalSet[A]): FunctionalSet[A] = Empty()
}

case class Cons[A](head: A, tail: FunctionalSet[A]) extends FunctionalSet[A] {
  override def contains(element: A): Boolean =
    this.head == element || tail.contains(element)

  override def +(element: A): FunctionalSet[A] =
    if (contains(element)) this
    else Cons(element, this)

  override def ++(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): FunctionalSet[B] =
    tail.map(f) + f(head)

  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] =
    f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): FunctionalSet[A] =
    if (predicate(head))
      tail.filter(predicate) + head
    else tail.filter(predicate)

  override def foreach(f: A => Unit): Unit =
    f(head)
    tail.foreach(f)

  override infix def -(element: A): FunctionalSet[A] =
    if (head == element) tail
    else tail - element + head

  override infix def --(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    filter(!anotherSet)

  override infix def &(anotherSet: FunctionalSet[A]): FunctionalSet[A] =
    filter(anotherSet)
}

object FunctionalSet {
  def apply[A](values: A*): FunctionalSet[A] = {
    @tailrec
    def buildSet(valuesSeq: Seq[A], acc: FunctionalSet[A]): FunctionalSet[A] =
      if (valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)

    buildSet(values, Empty())
  }
}

object FunctionalSetTests {
  def main(args: Array[String]): Unit = {
    val set = FunctionalSet(1, 2, 3, 4, 5)

    println(set.map(_ + 1).contains(1))
    println(set(5))
    println((set + 10).contains(10))
    set.flatMap(x => FunctionalSet(x, x + 1)).foreach(print)
    println("\n" + set.filter(_ > 5).apply(5))

    val someNumbers = FunctionalSet(1, 2, 3)
    val list        = (1 to 10).toList
    println((set - 3).apply(3))
    println((Empty[Int]() - 3).apply(3))

    (set -- someNumbers).foreach(print)

    (set & someNumbers).foreach(print)
    println("\n" + list.filter(someNumbers))

    val naturals = new PropertyBasedSet[Int](_ => true)
    println(naturals.contains(91239))
    println(!naturals.contains(0))
    println((!naturals + 1 + 2 + 3).contains(3))
  }
}
