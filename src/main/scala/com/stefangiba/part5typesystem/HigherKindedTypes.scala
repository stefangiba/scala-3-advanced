package com.stefangiba.part5typesystem

import scala.util.Try

object HigherKindedTypes {
  class HigherKindedType[F[_]] // HKT
  class HigherKindedType2[F[_], G[_], A]

  val hkExample  = new HigherKindedType[List]
  val hkExample2 = new HigherKindedType2[List, Option, String]

  // can use HKTs for methods as well

  // why: abstract libraries, e.g. Cats
  // example: Functor
  val list   = List(1, 2, 3)
  val option = Option(2)
  val aTry   = Try(42)

  val incrementedList   = list.map(_ + 1)   // List(2, 3, 4)
  val incrementedOption = option.map(_ + 1) // Some(3)
  val incrementedTry    = aTry.map(_ + 1)   // Success(43)

  // "duplicated" APIs
  def do10xList(list: List[Int]): List[Int]         = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(aTry: Try[Int]): Try[Int]            = aTry.map(_ * 10)

  // DRY principle
  // step 1: type class definition
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // step 2: type class instances
  given listFunctor: Functor[List] with
    def map[A, B](list: List[A])(f: A => B): List[B] = list.map(f)

  given optionFunctor: Functor[Option] with
    def map[A, B](option: Option[A])(f: A => B): Option[B] = option.map(f)

  given tryFunctor: Functor[Try] with
    def map[A, B](aTry: Try[A])(f: A => B): Try[B] = aTry.map(f)

  // step3: "user-facing" API
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  // step 4: extension methods

  extension [F[_]: Functor, A](container: F[A]) {
    def map[B](f: A => B): F[B] = summon[Functor[F]].map(container)(f)
  }

  def do10x_v2[F[_]: Functor](container: F[Int]): F[Int] = container.map(_ * 10)

  /*
    Exercise: implement a new type class on the same structure as Functor.
    In the general API, must use for-comprehensions
   */
  def combineList[A, B](listA: List[A], listB: List[B]): List[(A, B)] = for {
    a <- listA
    b <- listB
  } yield (a, b)

  def combineOption[A, B](
      optionA: Option[A],
      optionB: Option[B]
  ): Option[(A, B)] = for {
    a <- optionA
    b <- optionB
  } yield (a, b)

  def combineTry[A, B](tryA: Try[A], tryB: Try[B]): Try[(A, B)] = for {
    a <- tryA
    b <- tryB
  } yield (a, b)

  /*
    def combine[F[_]: SomeTypeClass, A, B](fa: F[A], fb: F[B]): F[(A, B)] = for {
      a <- fa
      b <- fb
    } yield (a, b)
   */
  // 1 - TC definition
  trait Monad[F[_]] extends Functor[F] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }

  // 2 - TC instance(s)
  given magicList: Monad[List] with {
    override def map[A, B](list: List[A])(f: A => B): List[B] = list.map(f)
    override def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] =
      list.flatMap(f)
  }

  // 3 - "user-facing" API
  def combine[F[_], A, B](fa: F[A], fb: F[B])(using
      magic: Monad[F]
  ): F[(A, B)] =
    magic.flatMap(fa)(a => magic.map(fb)(b => (a, b)))

  extension [F[_], A](container: F[A])(using magic: Monad[F]) {
    def flatMap[B](f: A => F[B]): F[B] = magic.flatMap(container)(f)
  }

  def combine_v2[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] = for {
    a <- fa
    b <- fb
  } yield (a, b)

  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
  }
}
