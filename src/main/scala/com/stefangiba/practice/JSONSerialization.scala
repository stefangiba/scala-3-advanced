package com.stefangiba.practice

import java.util.Date

object JSONSerialization {
  /*
    Users, posts, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data: numbers, strings, lists, objects
    2 - type class to convert data to intermediate data
    3 - serialize to JSON
   */

  sealed trait JSONValue {
    def stringify: String
  }

  case class JSONString(value: String) extends JSONValue {
    override def stringify: String = s"\"$value\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String =
      values.map(_.stringify).mkString("[", ", ", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue])
      extends JSONValue {
    override def stringify: String = values
      .map { case (key, value) =>
        s"\"$key\": ${value.stringify}"
      }
      .mkString("{", ", ", "}")
  }

  /*
    {
      "name": "John",
      "age": 22,
      "friends": [...],
      "latestPosts": { ... }
    }
   */

  val data = JSONObject(
    Map(
      "user" -> JSONString("Stefan"),
      "posts" -> JSONArray(
        List(
          JSONString("Scala is awesome!"),
          JSONNumber(42)
        )
      )
    )
  )

  // part 2 - type class pattern
  // 1 - TC definition
  trait JSONConverter[A] {
    def convert(value: A): JSONValue
  }

  // 2 - TC instances for String, Int, Date, User, Post, Feed
  given stringConverter: JSONConverter[String] with
    override def convert(string: String): JSONValue = new JSONString(string)

  given intConverter: JSONConverter[Int] with
    override def convert(int: Int): JSONValue = new JSONNumber(int)

  given dateConverter: JSONConverter[Date] with
    override def convert(date: Date): JSONValue =
      JSONConverter[String].convert(date.toString())

  given userConverter: JSONConverter[User] with
    override def convert(user: User): JSONValue = JSONObject(
      Map(
        "name"  -> JSONConverter[String].convert(user.name),
        "age"   -> JSONConverter[Int].convert(user.age),
        "email" -> JSONConverter[String].convert(user.email)
      )
    )

  given postConverter: JSONConverter[Post] with
    override def convert(post: Post): JSONValue = JSONObject(
      Map(
        "content" -> JSONConverter[String].convert(post.content),
        "created" -> JSONConverter[Date].convert(post.createdAt)
      )
    )

  given feedConverter: JSONConverter[Feed] with
    override def convert(feed: Feed): JSONValue = JSONObject(
      Map(
        "user"  -> JSONConverter[User].convert(feed.user),
        "posts" -> JSONArray(feed.posts.map(JSONConverter[Post].convert))
      )
    )

  // 3 - user-facing API
  object JSONConverter {
    def convert[A](value: A)(using converter: JSONConverter[A]): JSONValue =
      converter.convert(value)

    def apply[T](using instance: JSONConverter[T]): JSONConverter[T] = instance
  }

  // example
  val now  = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@rockthejvm.com")
  val feed = Feed(
    john,
    List(
      Post("hello", now),
      Post("look at this cute puppy", now)
    )
  )

  // 4 - extension methods
  object JSONSyntax {
    extension [T](value: T) {
      def toJSON(using converter: JSONConverter[T]): JSONValue =
        converter.convert(value)
    }
  }

  def main(args: Array[String]): Unit = {
    println(data.stringify)
    println(JSONConverter.convert(feed).stringify)

    import JSONSyntax.*
    println(feed.toJSON.stringify)
  }
}
