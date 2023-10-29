package com.stefangiba.part5typesystem

import com.stefangiba.part1as.Recap.Animal
import com.stefangiba.part2afp.FunctionalCollections.element

object TypeMembers {
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    // val, var, def, class, trait, object
    type AnimalType              // abstract type member
    type BoundedAnimal <: Animal // abstract type member with a type bound
    type SuperBoundedAnimal >: Dog <: Animal // abstract type member with two type bounds
    type AnimalAlias  = Cat // type alias
    type NestedOption = List[Option[Option[Int]]]
  }

  class MoreConcreteAnimalCollection extends AnimalCollection {
    override type AnimalType = Dog
  }

  // using type members
  val ac                    = new AnimalCollection
  val animal: ac.AnimalType = ???

  // val cat: ac.BoundedAnimal = new Cat // does not work, BoundedAnimal might be Dog
  val dog: ac.SuperBoundedAnimal = new Dog // ok
  val cat: ac.AnimalAlias        = new Cat // ok, Cat == AnimalAlias

  // establish relationships between types
  // alternative to generics
  class LList[T] {
    def add(element: T): LList[T] = ???
  }
  class MyList {
    type T
    def add(element: T): MyList = ???
  }

  // .type
  type CatType = cat.type
  val newCat: CatType = cat

  def main(args: Array[String]): Unit = {}
}
