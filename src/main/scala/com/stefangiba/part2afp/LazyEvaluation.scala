package com.stefangiba.part2afp

object LazyEvaluation {

  lazy val x: Int = {
    println("Hello")
    42
  }

  // Lazy DELAYS the evaluation of a value until the first use
  // evaluation occurs ONCE

  // call by need = call by name + lazy values
  private def byNameMethod(n: => Int): Int = n + n + n + 1

  private def retrieveMagicValue(): Int = {
    println("Waiting...")
    Thread.sleep(1000)
    42
  }

  private def demoByName(): Unit = {
    println(byNameMethod(retrieveMagicValue()))
  }

  private def byNeedMethod(n: => Int): Int = {
    lazy val lazyN = n // memoization
    lazyN + lazyN + lazyN + 1
  }

  private def demoByNeed(): Unit = {
    println(byNeedMethod(retrieveMagicValue()))
  }

  // withFilter
  private def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  private def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  private val numbers = List(1, 25, 40, 5, 23)

  private def demoFilter(): Unit = {
    val lt30 = numbers.filter(lessThan30)
    val gt20 = lt30.filter(greaterThan20)
    println(gt20)
  }

  private def demoWithFilter(): Unit = {
    val lt30 = numbers.withFilter(lessThan30)
    val gt20 = lt30.withFilter(greaterThan20)
    println(gt20.map(identity))
  }

  private def demoForComprehension(): Unit = {
    val forComp = for {
      n <- numbers if lessThan30(n) && greaterThan20(n)
    } yield n

    println(forComp)
  }

  def main(args: Array[String]): Unit = {
    println(x)
    println(x)

    demoByName()
    demoByNeed()

    demoFilter()
    demoWithFilter()
    demoForComprehension()
  }
}
