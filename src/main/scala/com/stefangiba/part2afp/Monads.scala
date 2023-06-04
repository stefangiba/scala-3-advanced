package com.stefangiba.part2afp

import scala.annotation.{tailrec, targetName}

object Monads {

  def listStory(): Unit = {
    val list = List(1, 2, 3)

    val listMultiply = for {
      x <- List(1, 2, 3)
      y <- List(4, 5, 6)
    } yield x * y

    // for comprehensions = chains of map + flatMap
    val listMultiply_v2 =
      List(1, 2, 3).flatMap(x => List(4, 5, 6).map(y => x * y))

    val f    = (x: Int) => List(x, x + 1)
    val g    = (x: Int) => List(x, 2 * x)
    val pure = (x: Int) => List(x) // same as the list "constructor"

    // prop 1: left identity
    val leftIdentity =
      pure(42).flatMap(f) == f(42) // for every x, and for every f

    // prop 2: right identity
    val rightIdentity = list.flatMap(pure) == list // for every list

    // prop 3: associativity
    val associativity =
      list.flatMap(f).flatMap(g) == list.flatMap(f(_).flatMap(g))
  }

  def optionStory(): Unit = {
    val option = Option(42)

    val optionString = for {
      lang    <- Option("Scala")
      version <- Option(3)
    } yield s"$lang-$version"
    // identical
    val optionString_v2 =
      Option("Scala").flatMap(lang => Option(3).map(ver => s"$lang, $ver"))

    val f    = (x: Int) => Option(x + 1)
    val g    = (x: Int) => Option(2 * x)
    val pure = (x: Int) => Option(x)

    // prop 1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42) // for any x, for any f

    // prop 2: right identity
    val rightIdentity = option.flatMap(pure) == option // for any Option

    // prop 3: associativity
    val associativity =
      option.flatMap(f).flatMap(g) == option.flatMap(
        f(_).flatMap(g)
      ) // for any Option, f and g
  }

  // MONADS = chain independent computations

  // exercise: Is this a monad?
  // answer: IT IS A MONAD!
  // interpretation: ANY computation that might perform side effects
  case class IO[A](unsafeRun: () => A) {
    def map[B](f: A => B): IO[B] =
      IO(() => f(unsafeRun()))

    def flatMap[B](f: A => IO[B]): IO[B] =
      IO(() => f(unsafeRun()).unsafeRun())
  }

  object IO {
    @targetName("pure")
    def apply[A](value: => A): IO[A] = new IO(() => value)
  }

  private def possiblyMonadStory(): Unit = {
    val possiblyMonad = IO(42)

    val f = (x: Int) => IO(x + 1)
    val g = (x: Int) => IO(2 * x)

    // prop 1: left identity
    val leftIdentity =
      IO(42).flatMap(f).unsafeRun() == f(42)
        .unsafeRun() // for any x, for any f

    // prop 2: right identity
    val rightIdentity =
      possiblyMonad
        .flatMap(
          IO.apply
        )
        .unsafeRun() == possiblyMonad.unsafeRun()

    // prop 3: associativity
    val associativity =
      possiblyMonad.flatMap(f).flatMap(g).unsafeRun() == possiblyMonad
        .flatMap(
          f(_).flatMap(g)
        )
        .unsafeRun()

    println(leftIdentity)
    println(rightIdentity)
    println(associativity)

    val fs = (x: Int) =>
      IO {
        println("incrementing")
        x + 1
      }

    val gs = (x: Int) =>
      IO {
        println("doubling")
        x * 2
      }

    val associativity_v2 =
      possiblyMonad.flatMap(fs).flatMap(gs).unsafeRun() == possiblyMonad
        .flatMap(
          fs(_).flatMap(gs)
        )
        .unsafeRun()
  }

  def possiblyMonadExample(): Unit = {
    val possiblyMonad = IO {
      println("printing my first possibly monad")
      // do some computations
      42
    }

    val anotherPm: IO[String] = IO {
      println("my second possibly monad")
      "Scala"
    }

    // computations are DESCRIBED, not EXECUTED
    val forComp = for {
      num  <- possiblyMonad
      lang <- anotherPm
    } yield s"$num-$lang"
  }

  def main(args: Array[String]): Unit = {
    possiblyMonadStory()
    possiblyMonadExample()
  }
}
