package com.stefangiba.part3async

import com.stefangiba.part1as.DarkSugars.thread

object JVMConcurrencyProblems {

  private def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => { x = 2 })

    thread1.start();
    thread2.start();

    println(x)
  }

  case class BankAccount(var amount: Int)

  private def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.amount -= price
  }

  private def buySafe(
      bankAccount: BankAccount,
      thing: String,
      price: Int
  ): Unit = {
    bankAccount.synchronized { bankAccount.amount -= price }
  }

  private def demoBankingProblem(): Unit = {
    (1 to 10000).foreach(_ => {
      val account = BankAccount(50000)

      val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
      val thread2 = new Thread(() => buySafe(account, "iPhone", 4000))

      thread1.start()
      thread2.start()

      thread1.join()
      thread2.join()

      if (account.amount != 43000)
        println(s"AHA! I've just broken the bank ${account.amount}")
    })
  }

  /** Exercises
   * 1 - create "inception threads"
   * thread 1
    * -> thread 2
    *   -> thread 3
    *     ...
    * 2 - What's the min / max value of x?
    * 3 - "sleep fallacy": what's the value of message?
    */

  // 1 - inception threads
  def inceptionThreads(maxThreads: Int): Thread = {
    def helper(i: Int): Thread =
      new Thread(() => {
        if (i < maxThreads) {
          val newThread = helper(i + 1)
          newThread.start()
          newThread.join()
        }

        println(s"Hello from thread $i")
      })

    helper(1)
  }

  // 2
  /** max value = 100 - each thread increases x by 1
   * min value = 1 - all threads read x = 0 at the same time and add 1 to it
    */
  def minMaxX(): Unit = {
    var x       = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }

  // 3
  /**
    * almost always, message = "Scala is awesome"
    * is it guaranteed? NO
    * Obnoxious situation (possible):
      main thread:
        message = "Scala sucks"
        awesomeThread.start()
        sleep(1001) - yields execution

      awesome thread:
        sleep(1000) - yields execution

      OS gives the CPU to some important thread, takes > 2s
      OS gives the CPU back to the main thread

      main thread:
        println(message) => "Scala sucks"

      awesome thread:
        message = "Scala is awesome"
    */
  def sleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1001)
    // solution: join the worker thread
    awesomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
    runInParallel()
    demoBankingProblem()

    inceptionThreads(50).start()
    sleepFallacy()
  }
}
