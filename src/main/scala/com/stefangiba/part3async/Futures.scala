package com.stefangiba.part3async

import java.util.concurrent.Executors
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Random
import scala.util.Success
import scala.util.Try

object Futures {

  def calculateMeaningOfLife(): Int = {
    // simulate long compute
    Thread.sleep(1000)
    42
  }

  // thread pool (Java specific)
  val executor = Executors.newFixedThreadPool(4)

  // thread pool (Scala specific)
  given executionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(executor)

  // a future = an async computation that will finish at some point
  val future: Future[Int] =
    Future.apply(calculateMeaningOfLife())

  // Option[Try[Int]], because
  // - we don't know if we have a value
  // - if we do, it can be a failed computation
  val futureInstantResult: Option[Try[Int]] = future.value

  // callbacks
  future.onComplete {
    case Success(value) =>
      println(s"I've completed with the meaning of life: $value")
    case Failure(exception) =>
      println(s"My async computation failed: $exception")
  } // will be evaulated on SOME other thread

  // Functional composition

  case class Profile(id: String, name: String) {
    def sendMessage(anotherProfile: Profile, message: String) =
      println(s"$name sending message to ${anotherProfile.name}: $message")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "rtjvm.id.1-daniel" -> "Daniel",
      "rtjvm.id.2-jane"   -> "Jane",
      "rtjvm.id.2-mark"   -> "Mark"
    )

    val friends = Map(
      "rtjvm.id.2-jane" -> "rtjvm.id.2-mark"
    )

    val random = new Random

    def fetchProfile(id: String): Future[Profile] = Future {
      // fetch something from the database
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // problem: sending a message to my best friend

  def sendMessageToBestFriend(accountId: String, message: String): Unit = {
    // 1 - call fetchProfile
    // 2 - call fetchBestFriend
    // 3 - call profile.sendMessage(bestFriend)
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    profileFuture.onComplete {
      case Success(profile) =>
        val friendProfileFuture = SocialNetwork.fetchBestFriend(profile)
        friendProfileFuture.onComplete {
          case Success(friendProfile) =>
            profile.sendMessage(friendProfile, message)
          case Failure(e) =>
            e.printStackTrace()
        }
      case Failure(e) => e.printStackTrace()
    }
  }

  // onComplete is a hassle.
  // solution: Functional composition
  val janeProfileFuture: Future[Profile] =
    SocialNetwork.fetchProfile("rtjvm.id.2-jane")
  // map transforms value contained inside the future, ASYNCHRONOUSLY
  // the lambda will be evaluated on SOME other thread when the initial future completes
  val janeFuture: Future[String] = janeProfileFuture.map(_.name)
  val janeBestFriend: Future[Profile] =
    janeProfileFuture.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val janeBestFriendFilter: Future[Profile] =
    janeBestFriend.filter(profile => profile.name.startsWith("Z"))

  def sendMessageToBestFriend_v2(accountId: String, message: String): Unit = {
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    val bestFriendFuture =
      profileFuture.flatMap { profile =>
        SocialNetwork.fetchBestFriend(profile).map { bestFriend =>
          profile.sendMessage(bestFriend, message)
        }
      }
  }

  def sendMessageToBestFriend_v3(accountId: String, message: String): Unit =
    for {
      profile    <- SocialNetwork.fetchProfile(accountId)
      bestFriend <- SocialNetwork.fetchBestFriend(profile)
    } yield profile.sendMessage(bestFriend, message)

  // fallbacks
  val profileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case _: Throwable => Profile("rtjvm.id.0-dummy", "Forever Alone")
  }

  // if it fails, runs partial function, and returns whatever result of second future
  val fetchedProfileNoMatterWhat =
    SocialNetwork.fetchProfile("unknown id").recoverWith { case _: Throwable =>
      SocialNetwork.fetchProfile("rtjvm.id.0-dummy")
    }

  // if second future fails as well, the exception will be returned from the first future
  val fallbackProfile = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("rtjvm.id.0-dummy"))

  // Block for a future
  // Only necessary when you cannot continue until a future's result has been computed

  case class User(name: String)
  case class Transaction(
      sender: String,
      receiver: String,
      amount: Double,
      status: String
  )

  object BankingApp {
    // "APIs"
    def fetchUser(name: String): Future[User] = Future {
      // simulate some DB fetching
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(
        user: User,
        merchantName: String,
        amount: Double
    ): Future[Transaction] = Future {
      // simulate payment
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    // "external API"
    def purchase(
        username: String,
        item: String,
        merchantName: String,
        price: Double
    ): String = {
      /*
        1. fetch user
        2. create transaction
        3. wait for transaction to finish
       */
      val transactionStatusFuture = for {
        user        <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, price)
      } yield transaction.status

      // blocking call
      Await.result(
        transactionStatusFuture,
        2.seconds
      ) // throws TimeoutException if the future doesn't finish within 2 secs
    }
  }

  // PROMISES

  def demoPromises() = {
    val promise      = Promise[Int]()
    val futureInside = promise.future

    // thread 1 - "consumer": monitor the future for completion
    futureInside.onComplete {
      case Success(value) =>
        println(s"[consumer] I've just been completed with $value")
      case Failure(exception) => exception.printStackTrace()
    }

    // thread 2 - "producer"
    val producerThread = new Thread(() => {
      println("[producer] Crunching numbers...")
      Thread.sleep(1000)
      // "fulfill" the promise
      promise.success(42)
      println("[producer] I'm done")
    })

    producerThread.start()
  }

  /**
    * Exercises
    * 1. Fulfill a future IMMEDIATELY with a value
    * 2. in sequence: make sure the first future has been completed before returning the second
    * 3. race(fa, fb) => new Future with the value of the first Future to complete
    * 4. last(fa, fb) => new Future containing the value of the last Future to complete
    * 5. retry a action returning a Future until a predicate holds true
    */

  // 1
  def completeImmediately[A](value: A): Future[A] = Future(value)
  def completeImmediately_v2[A](value: A): Future[A] =
    Future.successful(value) // synchronous completion

  // 2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = for {
    _      <- first
    result <- second
  } yield result

  // 3
  def race[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val promise = Promise[A]()

    f1.onComplete(result => promise.tryComplete(result))
    f2.onComplete(result => promise.tryComplete(result))

    promise.future
  }

  // 4
  def last[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val bothPromise = Promise[A]()
    val lastPromise = Promise[A]()

    def checkAndComplete(result: Try[A]): Unit =
      if (!bothPromise.tryComplete(result)) lastPromise.complete(result)

    f1.onComplete(checkAndComplete)
    f2.onComplete(checkAndComplete)

    lastPromise.future
  }

  // 5
  def retryUntil[A](
      action: () => Future[A],
      predicate: A => Boolean
  ): Future[A] =
    action().filter(predicate).recoverWith { case _: Throwable =>
      retryUntil(action, predicate)
    }

  def testRetries(): Unit = {
    val random = new Random
    val action = () =>
      Future {
        Thread.sleep(100)
        val nextValue = random.nextInt(100)
        println(s"Generated $nextValue")
        nextValue
      }
    val predicate = (x: Int) => x < 10

    retryUntil(action, predicate).foreach(finalResult =>
      println(s"Final result: $finalResult")
    )
  }

  def main(args: Array[String]): Unit = {
    println(future.value) // inspect the value of the future RIGHT NOW
    sendMessageToBestFriend_v3("rtjvm.id.2-jane", "Nice to talk to you again!")

    println("Purchasing...")
    println(BankingApp.purchase("daniel-234", "shoes", "merchan-987", 3.56))
    println("Purchase complete!")

    demoPromises()

    lazy val fast = Future {
      Thread.sleep(100)
      1
    }

    lazy val slow = Future {
      Thread.sleep(200)
      2
    }

    race(fast, slow).foreach(result => println(s"first is $result"))
    last(fast, slow).foreach(result => println(s"last is $result"))

    testRetries()

    Thread.sleep(3000)

    executor.shutdown()
  }
}
