package com.stefangiba.part5typesystem

import com.stefangiba.part1as.Recap.Crocodile
import com.stefangiba.part2afp.FunctionalCollections.element

object VariancePositions {
  class Animal
  class Dog       extends Animal
  class Cat       extends Animal
  class Crocodile extends Animal

  // 1 - type bounds

  class Cage[A <: Animal]
  // val cage = new Cage[String] // not ok, because String is not a subtype of Animal
  val realCage = new Cage[Dog] // ok, Dog <: Animal

  class WeirdContainer[A >: Animal] // A must be a supertype of Animal

  // 2 - variance positions

  // DOES NOT compile
  // class Vet[-T](val favoriteAnimal: T) // type of val fields are in COVARIANT position

  /*
    val garfield = new Cat
    val vet: Vet[Animal] = new Vet[Animal](garfield)
    val dogVet: Vet[Dog] = vet // posible, vet is Vet[Animal]
    val dog = dogVet.favoriteAnimal // must be a Dog, type conflict!
   */

  // types of var fields are in COVARIANT POSITION
  // (same reason)

  // types of var fields are in CONTRAVARIANT position
  // class MutableOption[+T](var contents: T)

  /*
    val maybeAnimal: MutableOption[Animal] = new MutableOption[Dog](new Dog)
    maybeAnimal.contents = new Cat // type conflict!
   */

  // types of methods arguments are in CONTRAVARIANT position
  // class MyList[+T] {
  //   def add(element: T): MyList[T] = ???
  // }
  class Vet[-T] {
    def heal(animal: T): Boolean = true
  }

  /*
    val animals: MyList[Animal] = new MyList[Cat]
    val biggerListOfAnimals = animals.add(new Dog) // type conflict
   */

  // method return types are in COVARIANT position
  // abstract class Vet2[-T] {
  //   def rescueAnimal(): T
  // }

  /*
    val vet: Vet2[Animal] = new Vet2[Animal] {
      override def rescueAnimal(): Animal = new Cat
    }
    val lassiesVet: Vet2[Dog] = vet // Vet2[Animal]
    val rescueDog: Dog = lassiesVet.rescueAnimal() // must be a Dog, type conflict!
   */

  // 3 - solving variance positions problems
  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
    def add[B >: A](element: B): LList[B] // widen the type
  }

  /*
    val animals: LList[Cat] = list of cats
    val newAnimals: List[Animal] = animals.add(new Dog)
   */

  class Vehicle
  class Car      extends Vehicle
  class Supercar extends Car
  class RepairShop[-A <: Vehicle] {
    def repair[B <: A](vehicle: B): B = vehicle // narrowing the type
  }

  val myRepairShop: RepairShop[Car] = new RepairShop[Vehicle]
  val myBeatupVw                    = new Car
  val freshCar       = myRepairShop.repair(myBeatupVw) // works, returns a Car
  val damagedFerrari = new Supercar
  val freshFerrari   = myRepairShop.repair(damagedFerrari)

  def main(args: Array[String]): Unit = {}
}
