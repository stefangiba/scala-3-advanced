package com.stefangiba.practice

import scala.annotation.{tailrec, targetName}

// Write a lazily evaluated, potentially INFINITE linked list
abstract class LzList[A] {
  def isEmpty: Boolean
  def head: A
  def tail: LzList[A]

  // utilities
  @targetName("prepend")
  def #::(element: A): LzList[A] // prepending
  @targetName("concat")
  infix def ++(another: => LzList[A]): LzList[A]

  // classics
  def foreach(f: A => Unit): Unit =
    @tailrec
    def aux(lzList: LzList[A]): Unit =
      if (lzList.isEmpty) ()
      else {
        f(lzList.head)
        aux(lzList.tail)
      }

    aux(this)

  def map[B](f: A => B): LzList[B]
  def flatMap[B](f: A => LzList[B]): LzList[B]
  def filter(predicate: A => Boolean): LzList[A]
  def withFilter(predicate: A => Boolean): LzList[A] =
    filter(predicate)

  def take(n: Int): LzList[A] // takes the first n elements from this lazy list
  def takeAsList(n: Int): List[A] = take(n).toList
  def toList: List[A] =
    @tailrec
    def toListAux(remaining: LzList[A], acc: List[A]): List[A] =
      if (remaining.isEmpty) acc.reverse
      else toListAux(remaining.tail, remaining.head :: acc)

    toListAux(tail, List(head))
}

case class LzEmpty[A]() extends LzList[A] {
  override def isEmpty: Boolean = true
  override def head: A          = throw new NoSuchElementException
  override def tail: LzList[A]  = throw new NoSuchElementException

  @targetName("prepend")
  override def #::(element: A): LzList[A] = new LzCons[A](element, this)
  @targetName("concat")
  infix override def ++(another: => LzList[A]): LzList[A] = another

  override def map[B](f: A => B): LzList[B]               = LzEmpty[B]()
  override def flatMap[B](f: A => LzList[B]): LzList[B]   = LzEmpty[B]()
  override def filter(predicate: A => Boolean): LzList[A] = this

  override def take(n: Int): LzList[A] =
    if (n == 0) this
    else
      throw new RuntimeException(
        s"Cannot take $n elements from empty lazy list"
      )
}

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {
  override def isEmpty: Boolean = false

  override lazy val head: A         = hd
  override lazy val tail: LzList[A] = tl

  @targetName("prepend")
  override def #::(element: A): LzList[A] = new LzCons(element, this)
  @targetName("concat")
  infix override def ++(another: => LzList[A]): LzList[A] =
    new LzCons(head, tail ++ another)

  override def map[B](f: A => B): LzList[B] =
    new LzCons[B](f(head), tail.map(f))
  override def flatMap[B](f: A => LzList[B]): LzList[B] =
    f(head) ++ tl.flatMap(f) // breaks lazy evaluation
  override def filter(predicate: A => Boolean): LzList[A] =
    if (predicate(head)) new LzCons(head, tail.filter(predicate))
    else tail.filter(predicate) // TODO warning

  override def take(n: Int): LzList[A] =
    if (n <= 0) LzEmpty()
    if (n == 1) new LzCons(head, LzEmpty())
    else new LzCons(head, tail.take(n - 1))
}

object LzList {
  def empty[A]: LzList[A] = LzEmpty()

  def generate[A](start: A)(generator: A => A): LzList[A] =
    new LzCons[A](start, generate(generator(start))(generator))

  def from[A](list: List[A]): LzList[A] =
    list.reverse.foldLeft(empty)((currentList, newElement) =>
      new LzCons(newElement, currentList)
    )

  def apply[A](values: A*): LzList[A] = from(values.toList)
}

def fibonacci: LzList[BigInt] =
  def fibo(first: BigInt, second: BigInt): LzList[BigInt] =
    new LzCons[BigInt](first, fibo(second, first + second))

  fibo(1, 1)

def eratosthenes: LzList[Int] =
  def sieve(numbers: LzList[Int]): LzList[Int] =
    val head = numbers.head
    val tail = numbers.tail

    new LzCons(
      head,
      sieve(tail.filter(_ % numbers.head != 0))
    )

  sieve(LzList.generate(2)(_ + 1))

object LzListPlayground {
  def main(args: Array[String]): Unit = {
    val naturals = LzList.generate(1)(n => n + 1)

    println(naturals.head)
    println(naturals.tail.head)
    println(naturals.tail.tail.head)

    val first50k     = naturals.take(50000)
    val first50kList = first50k.toList
    first50k.foreach(println)
    println(first50kList)

    println(naturals.map(_ * 2).takeAsList(100))
    println(naturals.flatMap(x => LzList(x, x + 1)).takeAsList(100))
    println(naturals.filter(_ < 10).takeAsList(9))

    val combinationsLazy = for {
      number <- LzList(1, 2, 3)
      string <- LzList("black", "white")
    } yield s"$number-$string"

    println(combinationsLazy.toList)
    println(fibonacci.take(100).toList)
    println(eratosthenes.take(1000).foreach(println))
  }
}
