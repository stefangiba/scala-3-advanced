package com.stefangiba.part4context

object OrganizingContextualAbstractions {
  val list        = List(1, 2, 3, 4)
  val orderedList = list.sorted

  // compiler fetches givens / extension methods

  // 1 - local scope
  given reverseOrdering: Ordering[Int] with {
    override def compare(x: Int, y: Int): Int = y - x
  }

  // 2 - imported scope
  case class Person(name: String, age: Int)
  val people = List(Person("Steve", 30), Person("Amy", 22), Person("John", 67))

  object PersonGivens {
    given ageOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int = y.age - x.age
    }

    extension (p: Person)
      def greet: String = s"Hello, I'm ${p.name}, I'm so glad to meet you!"
  }

  // a - import explicitly
  // import PersonGivens.ageOrdering
  // b - import a given for a particular type
  // import PersonGivens.{given Ordering[Person]}
  // c - import all givens
  // import PersonGivens.given

  // warning: import PersonGivens.* does not also import given instances

  // 3 - companion objects of all types involved in the method signature
  /*
    - Ordering
    - List
    - person
   */
  // def sorted[B >: A](using ord: Ordering[B]): List[B]

  object Person {
    given byNameOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person): Int = x.name.compareTo(y.name)

    extension (p: Person) def greet: String = s"Hello, I'm ${p.name}!"
  }

  val sortedPeople = people.sorted

  /*
    Good practice tips:
      1) When you have a "default" given (only ONE that makes sense), add it in the companion object type.
      2) When you have MANY possible givens, but ONE that is dominant (used most), add that in the companion and
      the rest in separate objects to be imported
      3) When you have MANY possible givens and NO ONE is dominant, add them in separate objects and import them explicitly.
   */

  // same principles apply to extension methods as well

  /*
   * Exercises: create given instances for Ordering[Purchase]
   * - ordering by total price = 50% of code base
   * - ordering by unit count, descending = 25% of code base
   * - ordering by unit price, ascending = 25% of code base
   */

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    given totalPriceOrdering: Ordering[Purchase] with {
      override def compare(x: Purchase, y: Purchase): Int =
        x.totalPrice.compare(y.totalPrice)
    }

    extension (p: Purchase) def totalPrice: Double = p.nUnits * p.unitPrice
  }

  object PurchaseGivens {
    given unitCountDescendingOrdering: Ordering[Purchase] with {
      override def compare(x: Purchase, y: Purchase): Int = y.nUnits - x.nUnits
    }

    given unitPriceAscendingOrdering: Ordering[Purchase] with {
      override def compare(x: Purchase, y: Purchase): Int =
        x.unitPrice.compare(y.unitPrice)
    }
  }

  def main(args: Array[String]): Unit = {
    println(orderedList)
    println(sortedPeople)
    import PersonGivens.* // incudes extension methods
    println(Person("Stefan", 24).greet)
  }
}
