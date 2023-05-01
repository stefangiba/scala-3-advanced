package com.stefangiba.part2afp

object CurryingPAFs {

  // currying
  private val superAdder: Int => Int => Int = x => y => x + y

  private val add3: Int => Int = superAdder(3)
  private val eight: Int       = add3(5)
  private val eight_v2: Int    = superAdder(3)(5)

  // curried methods
  private def curriedAdder(x: Int)(y: Int): Int = x + y

  // methods != function values

  // converting methods to functions = eta-expansion
  private val add4: Int => Int = curriedAdder(4)
  private val nine: Int        = add4(5)

  private def increment(x: Int): Int     = x + 1
  val list: List[Int]                    = List(1, 2, 3)
  private val incrementedList: List[Int] = list.map(increment)

  // underscores are powerful
  private def concatenator(a: String, b: String, c: String): String = a + b + c
  private val insertName: String => String = concatenator(
    "Hello, my name is ",
    _: String,
    ". I'm going to show you a nice Scala trick!"
  )

  private val stefanGreeting: String = insertName("Stefan")

  private val fillInTheBlanks: (String, String) => String =
    concatenator(
      _: String,
      "Stefan",
      _: String
    ) // (x, y) => concatenator(x, "Stefan", y)

  // Exercises

  private val simpleAddFunction                     = (x: Int, y: Int) => x + y
  private def simpleAddMethod                       = (x: Int, y: Int) => x + y
  private def curriedAddMethod(x: Int)(y: Int): Int = x + y

  // 1 - obtain add8 function x => x + 7 out of these 3 definitions
  private val add7: Int => Int    = simpleAddFunction(_: Int, 7)
  private val add7_v2: Int => Int = simpleAddFunction(7, _: Int)
  private val add7_v3: Int => Int = simpleAddMethod(7, _: Int)
  private val add7_v4: Int => Int = simpleAddMethod(_: Int, 7)
  private val add7_v5: Int => Int = curriedAddMethod(7)
  private val add7_v6: Int => Int = curriedAddMethod(_: Int)(7)

  // 2 - process a list of numbers and return their String representations under different formats
  // step 1: create a curried formatting method with a formatting string and a value
  // step 2: process a list of numbers with various formats
  private val piWith2Dec = "%4.6f".formatted(Math.PI)

  private def formatter(fmt: String)(number: Double): String =
    fmt.format(number)

  private val someDecimals = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  // methods vs functions + by-name vs 0-lambdas
  private def byName(n: => Int)      = n + 1
  private def byLambda(f: () => Int) = f() + 1
  private def method: Int            = 42
  private def parenMethod(): Int     = 42

  def main(args: Array[String]): Unit = {
    println(stefanGreeting)
    println(piWith2Dec)

    println(someDecimals.map(formatter("%4.2f")))
    println(someDecimals.map(formatter("%8.6f")))
    println(someDecimals.map(formatter("%14.12f")))

    println(byName(23))     // ok
    println(byName(method)) // eta-expanded? NO - method is INVOKED here
    println(byName(parenMethod()))
//    println(byName(parenMethod)) // NOT OK

//    println(byLambda(23)) // NOT OK
    println(byLambda(() => 23))
//    println(byLambda(method)) // eta-expansion is not possible
    println(byLambda(parenMethod))
  }
}
