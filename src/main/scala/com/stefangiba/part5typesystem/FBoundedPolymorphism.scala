package com.stefangiba.part5typesystem

object FBoundedPolymorphism {
  object Problem {
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Animal] = List(new Cat, new Dog) // <- problem !!
    }

    class Dog extends Animal {
      override def breed: List[Animal] = List(new Dog, new Dog, new Dog)
    }

    // losing type safety
  }

  object NaiveSolution {
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Cat] = List(new Cat, new Cat)
    }

    class Dog extends Animal {
      override def breed: List[Dog] = List(new Dog, new Dog, new Dog)
    }

    // I have to write the proper type signatures
    // problem: I want the compiler to help
  }

  object FBP {
    trait Animal[A <: Animal[A]] { // recursive type, F-bounded polymorphism
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = List(new Cat, new Cat)
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = List(new Dog, new Dog, new Dog)
    }

    // mess up FBP
    class Crocodile extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ???
    }
  }

  // example: some ORM libraries
  trait Entity[E <: Entity[E]]

  // example: Java sorting library
  class Person extends Comparable[Person] { // FBP
    override def compareTo(o: Person): Int = ???
  }

  // FBP + self-types
  object FBPSelf {
    trait Animal[A <: Animal[A]] { self: A =>
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] { // Cat == Animal[Cat]
      override def breed: List[Animal[Cat]] = List(new Cat, new Cat)
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = List(new Dog, new Dog, new Dog)
    }

    // class Crocodile extends Animal[Dog] { // not ok, I must also extend Dog
    //   override def breed: List[Animal[Dog]] = ???
    // }

    // one level deeper
    trait Fish extends Animal[Fish]
    class Cod extends Fish {
      override def breed: List[Animal[Fish]] = List(new Cod, new Cod)
    }
    class Shark extends Fish {
      override def breed: List[Animal[Fish]] =
        List(new Shark, new Shark, new Shark)
    }

    // solution level 2
    trait FishL2[A <: FishL2[A]] extends Animal[FishL2[A]] { self: A => }
    class Tuna extends FishL2[Tuna] {
      override def breed: List[Animal[FishL2[Tuna]]] =
        List(new Tuna, new Tuna)
    }
    class Swordfish extends FishL2[Swordfish] {
      override def breed: List[Animal[FishL2[Swordfish]]] =
        List(new Swordfish, new Swordfish)
    }
  }

  def main(args: Array[String]): Unit = {}
}
