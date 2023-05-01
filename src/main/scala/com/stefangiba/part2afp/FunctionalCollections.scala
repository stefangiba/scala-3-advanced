package com.stefangiba.part2afp

object FunctionalCollections {
  // Sets are functions `A => Boolean`
  val set: Set[String]          = Set("I", "love", "Scala", "!")
  val setContainsScala: Boolean = set("Scala")

  // Seq "extends" PartialFunction[Int => A]
  val seq: Seq[Int] = Seq(1, 2, 3, 4)
  val element: Int  = seq(2)
//  val nonExistingElement: Int = seq(100) // throws OOBException

  // Map "extends" PartialFunction[K, V]
  val phoneBook: Map[String, Int] = Map(
    "Alice" -> 12345,
    "Bob"   -> 67890
  )
  val alicePhoneNumber: Int = phoneBook("Alice")
//  val stefanPhoneNumber: Int = phoneBook("Stefan") // throws NoSuchElementException

  def main(args: Array[String]): Unit = {}
}
