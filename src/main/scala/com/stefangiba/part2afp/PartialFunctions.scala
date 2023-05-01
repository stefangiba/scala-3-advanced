object PartialFunctions {
  val function: Int => Int = x => x + 1
  val fussyFunction = (x: Int) =>
    x match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }

  // partial function
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  val canCallOn37           = partialFunction.isDefinedAt(37)
  val liftedPartialFunction = partialFunction.lift

  val anotherPartialFunction: PartialFunction[Int, Int] = { case 45 => 86 }
  val partialFunctionChain = partialFunction.orElse(anotherPartialFunction)

  // HOFs accept partial functions as arguments
  val list = List(1, 2, 3, 4)
  val changedList = list.map(x =>
    x match {
      case 1 => 4
      case 2 => 3
      case 3 => 45
      case 4 => 67
      case _ => 0
    }
  )
  // possible because PartialFunction[A, B] extends Function1[A, B]
  val changedList_v2 = list.map({
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  })

  val changedList_v3 = list.map {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  }

  case class Person(name: String, age: Int)

  val kids = List(Person("Alice", 3), Person("Bobby", 5), Person("Jane", 4))
  val kidsGrowingUp = kids.map { case Person(name, age) =>
    Person(name, age + 1)
  }

  def main(args: Array[String]): Unit = {
    println(liftedPartialFunction(5))
    println(liftedPartialFunction(37))
    println(partialFunction(2))
    println(partialFunctionChain(45))
    println(partialFunction(33)) // FAILS
  }
}
