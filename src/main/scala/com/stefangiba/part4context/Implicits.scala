package com.stefangiba.part4context

object Implicits {
  // given / using clauses
  // extension methods
  // implicit conversions

  // implicit arg -> using clause
  // implicit val -> given declaration
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(implicit semigroup: Semigroup[A]): A =
    list.reduce(semigroup.combine)

  implicit val intSemigroup: Semigroup[Int] = new Semigroup[Int] {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val sumOf10 = combineAll((1 to 10).toList)

  // implicit class => extension methods
  implicit class MyRichInteger(number: Int) {
    def isEven: Boolean = number % 2 == 0
  }

  val questionOfMyLife = 23.isEven // new MyRichInteger(23).isEven

  // implicit conversions
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name"
  }

  // SUPER DANGEROUS
  implicit def string2Person(name: String): Person = Person(name)

  val stefanSaysHi = "Stefan".greet // string2Person("Stefan").greet

  // impicit def => synthesize NEW implicit values

  implicit def semigroupOfOption[A](implicit
      semigroup: Semigroup[A]
  ): Semigroup[Option[A]] =
    new Semigroup[Option[A]] {
      override def combine(x: Option[A], y: Option[A]): Option[A] = for {
        xVal <- x
        yVal <- y
      } yield semigroup.combine(xVal, yVal)
    }

  // Organizing implicits == organizing contextual abstractions
  // import yourPackage.* // also imports implicits

  /*
    Why implicits will be phased out:
      - the implicit keyword has many different meanings
      - conversions are easy to abuse
      - implicits are very hard to track down while debugging (givens also not trivial, but they are explicitly imported)
   */

  /*
    Contextual abstractions:
      - given/using clauses
      - extension methods
      - explicitly declared implicit conversions
   */

  def main(args: Array[String]): Unit = {
    println(sumOf10)
  }
}
