package com.stefangiba.part3async

import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector
import scala.concurrent.ExecutionContext

object ParallelCollections {
  val list            = (1 to 1000000).toList
  val incrementedList = list.map(_ + 1)

  val parList: ParSeq[Int] = list.par
  val parIncrementedList =
    parList.map(_ + 1) // map, flatMap, filter, foreach, reduce, fold

  /*
  Applicable for
    - Seq
    - Vector
    - Array
    - Maps
    - Sets

  Use-case: faster processing
   */

  // parallel collection built explicitly
  val parVector = ParVector(1, 2, 3, 4, 5, 6)

  def measure[A](expression: => A): Long = {
    val startTime = System.currentTimeMillis()
    expression // force evaluation
    System.currentTimeMillis() - startTime
  }

  def compareListTransformation(): Unit = {
    val list = (1 to 40_000_000).toList
    println("List creation done!")

    val serialTime = measure(list.map(_ + 1))
    println(s"Serial time: $serialTime")

    val parallelTime = measure(list.par.map(_ + 1))
    println(s"Parallel time: $parallelTime")
  }

  def demoUndefinedOrder(): Unit = {
    val list = (1 to 1000).toList
    val reduction =
      list.reduce(_ - _) // usually a bad idea to use non-associative operators

    val parallelReduction = list.par.reduce(_ - _)
    println(s"Sequential reduction $reduction")
    println(s"Parallel reduction $parallelReduction")
  }

  def demoDefinedOrder(): Unit = {
    val strings = List(
      "I",
      "love",
      "parallel",
      "collections",
      "but",
      "I",
      "must",
      "be",
      "careful"
    )
    val concatenation         = strings.reduce(_ + " " + _)
    val parallelConcatenation = strings.par.reduce(_ + " " + _)

    println(s"Sequential concatenation: $concatenation")
    println(s"Parallel concatenation: $parallelConcatenation")
  }

  def demoRaceConditions(): Unit = {
    var sum = 0
    (1 to 1000).toList.par.foreach(elem => sum += elem)
    println(sum)
  }

  def main(args: Array[String]): Unit = {
    demoRaceConditions()
  }
}
