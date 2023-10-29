package com.stefangiba.part5typesystem

import reflect.Selectable.reflectiveSelectable

object StructuralTypes {
  type SoundMaker = { // structural type
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("vroom!")
  }

  val dog: SoundMaker = new Dog // ok
  val car: SoundMaker = new Car // ok
  // compile-time duck typing

  // type refinements
  abstract class Animal {
    def eat(): String
  }

  type WalkingAnimal = Animal { // refined type
    def walk(): Int
  }

  // why: creating type-safe APIs for existing types following the same structure, but with no connection to each other
  type JavaCloseable = java.io.Closeable
  class CustomCloseable {
    def close(): Unit         = println("ok, ok, I'm closing...")
    def closeSilently(): Unit = println("not making a sound, I promise")
  }

  // def closeResource(closeable: JavaCloseable | CustomCloseable): Unit =
  //   closeable.close() //  not ok

  // solution structural type
  type UnifiedCloseable = {
    def close(): Unit
  }

  def closeResource(closeable: UnifiedCloseable): Unit =
    closeable.close()

  val jCloseable = new JavaCloseable {
    override def close(): Unit = println("closing Java resource")
  }
  val customClosable = new CustomCloseable

  def closeResource_v2(closeable: { def close(): Unit }): Unit =
    closeable.close()

  def main(args: Array[String]): Unit = {
    dog.makeSound() // through reflection (slow)
    car.makeSound()

    closeResource(jCloseable)
    closeResource(customClosable)
  }
}
