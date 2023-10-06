package com.stefangiba.part3async

import java.util.concurrent.Executors

object JVMConcurrencyIntro {
  private def basicThreads(): Unit = {
    val runnable = new Runnable {
      override def run(): Unit = {
        println("waiting...")
        Thread.sleep(2000)
        println("running on some thread")
      }
    }

    // threads on the JVM
    val thread = new Thread(runnable)
    thread.start() // will run the runnable on some JVM thread
    // JVM thread === OS thread (soon to change via Project Loom)
    thread.join() // block until the thread finishes
  }

  // order of operations is NOT guaranteed
  private def orderOfExecution(): Unit = {
    val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
    val threadGoodbye = new Thread(() =>
      (1 to 5).foreach(_ => println("goodbye"))
    )

    threadHello.start()
    threadGoodbye.start()
  }

  // executors
  private def demoExecutors(): Unit = {
    val threadPool = Executors.newFixedThreadPool(4)

    // submit a computation
    threadPool.execute(() => println("something in the thread pool"))
    threadPool.execute(() => {
      Thread.sleep(1000)
      println("done after 1 second")
    })

    threadPool.execute { () =>
      Thread.sleep(1000)
      println("almost done")
      Thread.sleep(1000)
      println("done after 2 seconds")
    }
    threadPool.shutdown()
//    threadPool.execute(() =>
//      println("this should NOT appear")
//    ) // should throw an exception in the calling thread
  }

  def main(args: Array[String]): Unit = {
    basicThreads()
    orderOfExecution()
    demoExecutors()
  }
}
