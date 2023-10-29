package com.stefangiba.part5typesystem

object LiteralUnionIntersectionTypes {
  // 1 - literal types
  val number   = 3
  val three: 3 = 3

  def passNumber(n: Int) = println(n)
  passNumber(45)    // ok
  passNumber(three) // ok, 3 <: Int

  def passStrict(n: 3) = println(n)
  passStrict(three)
  // passStrict(45) // not ok, !(Int <: 3)

  // available for double, boolean, strings
  val pi: 3.14                  = 3.14
  val truth: true               = true
  val favoriteLanguage: "Scala" = "Scala"

  def doSomethingWithYourLife(meaning: Option[42]) = meaning.foreach(println)

  // 2 - union types
  val truthOr42: Boolean | Int = 43

  def ambivalentMethod(arg: String | Int) = arg match {
    case _: String => "a string"
    case _: Int    => "a number"
  } // PM complete

  val anotherNumber = ambivalentMethod(56)
  val string        = ambivalentMethod("Scala")

  // type inference - chooses a lowest common ancestor of the two types of the String | Int
  val stringOrInt                  = if (43 > 0) "a string" else 45
  val stringOrInt_v2: String | Int = if (43 > 0) "a string" else 45

  // union types + nulls
  type Maybe[T] = T | Null
  def handleMaybe(maybe: Maybe[String]): Int =
    if (maybe != null) maybe.length // flow typing
    else 0

  // type ErrorOr[T] = T | "error"
  // def handleResource(resource: ErrorOr[Int]): Unit =
  //   if (resource != "error") println(resource + 1) // flow typing doesn't work
  //   else println("error")

  // 3 - intersection types
  class Animal
  trait Carnivore
  class Crocodile extends Animal with Carnivore

  val carnivoreAnimal: Animal & Carnivore = new Crocodile

  trait Gadget {
    def use(): Unit
  }

  trait Camera extends Gadget {
    def takePicture(): Unit = println("Smile!")
    override def use()      = println("Snap")
  }

  trait Phone extends Gadget {
    def makePhoneCall(): Unit = println("Calling...")
    override def use()        = println("Ring")
  }

  def useSmartDevice(smartPhone: Camera & Phone): Unit = {
    smartPhone.takePicture()
    smartPhone.makePhoneCall()
    smartPhone.use() // which use() is being called
  }

  class SmartPhone extends Camera with Phone // diamond problem

  // intersection types + covariance
  trait HostConfig
  trait HostController {
    def get: Option[HostConfig]
  }

  trait PortConfig
  trait PortController {
    def get: Option[PortConfig]
  }

  // compiles
  def getConfigs(
      controller: HostController & PortController
  ): Option[HostConfig & PortConfig] = controller.get

  def main(args: Array[String]): Unit = {
    useSmartDevice(new SmartPhone)
  }
}
