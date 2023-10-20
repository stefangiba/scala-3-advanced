package com.stefangiba.part4context

object Givens {
  // list sorting
  val list        = List(4, 2, 3, 1)
  val orderedList = list.sorted

  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  val inverseOrderedList                  = list.sorted(descendingOrdering)

  // custom sorting
  case class Person(name: String, age: Int)
  val people = List(Person("Alice", 29), Person("Sarah", 34), Person("Jim", 23))

  given personOrdering: Ordering[Person] = new Ordering[Person] {
    override def compare(x: Person, y: Person): Int = x.name.compareTo(y.name)
  }

  val sortedPeople = people.sorted

  object PersonAltSyntax {
    given personOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int = x.name.compareTo(y.name)
    }
  }

  // using clauses
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)

  /*
    combineAll(List(1, 2, 3, 4))
    combineAll(people)
   */

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val firstSum = combineAll(List(1, 2, 3, 4))
  // val combineAllPeople = combineAll(people) // does not compile - no Combinator[Person] in scope

  // context bound
  def combineInGroupsOf3[A](list: List[A])(using
      Combinator[A]
  ): List[A] =
    list.grouped(3).map(combineAll).toList

  def combineInGroupsOf3_v2[A: Combinator](list: List[A]): List[A] =
    list.grouped(3).map(combineAll).toList

  // synthesize new given instaces based on existing ones
  given listOrdering(using intOrdering: Ordering[Int]): Ordering[List[Int]]
    with {
    override def compare(x: List[Int], y: List[Int]): Int =
      intOrdering.compare(x.sum, y.sum)
  }

  val listOfLists        = List(List(1, 2), List(1, 1), List(3, 4, 5))
  val nestedListsOrdered = listOfLists.sorted

  // ... with generics
  given listOrderingBasedOnCombinator[A](using ord: Ordering[A])(using
      combinator: Combinator[A]
  ): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]): Int =
      ord.compare(combineAll(x), combineAll(y))
  }

  // pass a regular value instead of a given
  val myCombinator = new Combinator[Int] {
    override def combine(x: Int, y: Int): Int = x * y
  }
  val listProduct = combineAll(List(1, 2, 3, 4))(using myCombinator)

  /*
    Exercises:
      1 - create a given for ordering Option[A] if you can order A
      2 - create a summoning method that fetches the given value of your particular type
   */

  given optionOrdering[A: Ordering]: Ordering[Option[A]] with {
    override def compare(x: Option[A], y: Option[A]): Int = (x, y) match {
      case (None, None) => 0
      case (None, _)    => -1
      case (_, None)    => 1
      case (Some(a), Some(b)) =>
        fetchGivenValue[Ordering[A]].compare(a, b) // or use "summon"
    }
  }

  def fetchGivenValue[A](using theValue: A): A = theValue

  def main(args: Array[String]): Unit = {
    println(orderedList)
    println(inverseOrderedList)
    println(sortedPeople)
    println(firstSum)
    println(nestedListsOrdered)
    println(List(Option(1), Option.empty, Option(3), Option(-1000)).sorted)
  }
}
