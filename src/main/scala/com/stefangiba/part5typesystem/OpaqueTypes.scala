package com.stefangiba.part5typesystem

import com.stefangiba.part3async.Futures.SocialNetwork

object OpaqueTypes {
  object SocialNetwork {
    // some data structures = "domain"
    opaque type Name = String

    object Name {
      def apply(str: String): Name = str
    }

    extension (name: Name) {
      def length: Int = name.length // use the String API
    }

    // inside, name <-> String
    def addFriend(person1: Name, person2: Name): Boolean =
      person1.length == person2.length // use the entire String API
  }

  // outside SocialNetwork, Name and String are NOT related
  import SocialNetwork.*
  // val name: Name = "Stefan" // will not compile

  // why: you don't need (or want) to have access to the enitre String API for the Name type

  object Graphics {
    opaque type Color                = Int // in hex
    opaque type ColorFilter <: Color = Int

    val Red: Color                    = 0xff000000
    val Green: Color                  = 0x00ff0000
    val Blue: Color                   = 0x0000ff00
    val halfTransparency: ColorFilter = 0x88 // 50%
  }

  import Graphics.*
  case class OverlayFilter(color: Color)

  val fadeLayer = OverlayFilter(halfTransparency) // ColorFilter <: Color

  // how can we create instances of opaque types + how to access their APIs

  // 1 - companion objects
  val name = Name("Stefan")

  // 2 - extension methods
  val nameLength = name.length // ok, because of extension methods

  def main(args: Array[String]): Unit = {}
}
