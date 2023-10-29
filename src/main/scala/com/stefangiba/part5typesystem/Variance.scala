package com.stefangiba.part5typesystem

object Variance {
  class Animal
  class Dog(name: String) extends Animal

  // Variance question for List: if Dog extends Animal, then should a List[Dog] "extend" a List[Animal]?
  // for List, YES - List is COVARIANT

  val lassie = new Dog("Lassie")
  val hachi  = new Dog("Hachi")
  val laika  = new Dog("Laika")

  val animal: Animal = lassie // ok, Dog <: Animal (Dog is a subtype of Animal)
  val dogs: List[Animal] = List(
    lassie,
    hachi,
    laika
  ) // ok, List[Dog] <: List[Animal] === List is Covariant

  // definition of covariant types
  class MyList[+A] // MyList is COVARIANT in A
  val listOfAnimals: MyList[Animal] = new MyList[Dog]

  // if NO, then the type is INVARIANT
  // INVARIANT = you cannot substitute a semigroup of one type for a semigroup of another type
  trait Semigroup[A] { // no marker = INVARIANT
    def combine(x: A, y: A): A
  }

  // Java generics
  // val javaList: java.util.ArrayList[Animal] = new java.util.ArrayList[Dog] // type mismatch: Java generics are all invariant

  // HELL NO - CONTRAVARIANCE
  // if Dog <: Animal, then Vet[Animal] <: Vet[Dog]
  trait Vet[-A] { // CONTRAVARIANT in A
    def heal(animal: A): Boolean
  }

  val vet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = {
      println("Hey there, you're all good")
      true
    }
  }
  val healLaika = vet.heal(laika)

  /*
    Rule of thumb:
      - if your type PRODUCES or RETRIEVES a value (e.g. a list), then it should be COVARIANT
      - if your type ACTS ON or CONSUMES a value (e.g a vet), then it should be CONTRAVARIANT
      - otherwise, INVARIANT
   */

  /*
  Exercises:
    1) Which types should be invariant, covariant or contravariant
    2) Add variance modifiers to this "library"
   */

  // 1)
  // produces values: COVARIANT
  class RandomGenerator[+A]
  // they allow retrieving values: COVARIANT
  class MyOption[+A]
  // consumes values and turns them into Strings: CONTRAVARIANT
  class JSONSerializer[-A]
  // consumes values of type A, CONTRAVARIANT, and produces values of type B, COVARIANT
  trait MyFunction[-A, +B]

  val function: MyFunction[Dog, Animal] = new MyFunction[Animal, Dog] {}

  // 2)
  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
  }

  case object EmptyList extends LList[Nothing] {
    override def head = throw new NoSuchElementException
    override def tail = throw new NoSuchElementException
  }

  case class Cons[+A](override val head: A, override val tail: LList[A])
      extends LList[A]

  val list: LList[Int]           = EmptyList
  val anotherList: LList[String] = EmptyList

  // Nothing <: A, then LList[Nothing] <: LList[A]

  def main(args: Array[String]): Unit = {}
}
