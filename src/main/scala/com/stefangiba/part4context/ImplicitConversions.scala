package com.stefangiba.part4context

// special import
import scala.language.implicitConversions

object ImplicitConversions {
  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }

  val stefan       = Person("Stefan")
  val stefanSaysHi = stefan.greet()

  given string2Person: Conversion[String, Person] with
    override def apply(name: String): Person = Person(name)

  val stefanSaysHi_v2 =
    "Stefan".greet() // Person("Stefan").greet() automatically by the compiler

  def processPerson(person: Person): String =
    if (person.name.startsWith("J")) "OK"
    else "NOT OK"

  val isJaneOk = processPerson(
    "Jane"
  ) // ok - compiler rewrites processPerson(Person("Jane"))

  /*
    - auto-box types
    - use multiple types for the same code interchangebly
   */

  def main(args: Array[String]): Unit = {}
}
