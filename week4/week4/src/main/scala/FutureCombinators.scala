import scala.annotation.tailrec
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object FutureCombinators {

  def sumOfThreeNumbers(): Future[Int] = {
    Future {
      Thread.sleep(1000)
      1
    }.flatMap { firstValue =>
      Future {
        Thread.sleep(2000)
        2
      }.flatMap { secondValue =>
        Future {
          Thread.sleep(3000)
          3
        }.map { thirdValue =>
          firstValue + secondValue + thirdValue
        }
      }
    }
  }

  def timed[T](block: => T): T = {
    val start = System.currentTimeMillis()
    val result = block
    val duration = System.currentTimeMillis() - start
    println(s"Time take: $duration")
    result
  }

  def retryRecursive[T](numTimes: Int)(block: => Future[T]): Future[T] = {
    if (numTimes == 0) {
      Future.failed(new Exception(s"Sorry, failed after $numTimes"))
    } else {
      block fallbackTo {
        retryRecursive(numTimes - 1)(block)
      }
    }
  }

  def retry[T](numTimes: Int)(block: => Future[T]): Future[T] = {
    val ns: List[Int] = (1 to numTimes).toList
    // We create () => block since we don't want the block() to execute just yet.
    // We delay the execution of the block, since its execution can have side effects.
    val attempts: List[() => Future[T]] = ns.map(_ => () => block)

    val failed: Future[T] = Future.failed(new Exception(s"Sorry, failed after $numTimes"))

    val result1: Future[T] = attempts.foldLeft(failed) ( (acc, block) =>  acc recoverWith  { case _ => block() } )
    result1

    /*
    val result: () => Future[T] = attempts.foldRight(() => failed) ((block, acc) =>
      () => {
        block() fallbackTo { acc() }
      }
    )

    // retry(3){ block }() = unfolds to
    // block_1 fallbackTo { block_2 fallbackTo { block_3 fallbackTo { failed } } }

    result()
    */

  }

  def fallbackTo[T](self: => Future[T], that: => Future[T]): Future[T] = {
    self recoverWith {
      case _ => that recoverWith {
        case _ => self
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val future = sumOfThreeNumbers()
    // val result: Int = timed(Await.result(future, 7 seconds))
    // println(s"result: $result")

    val result = retry(2)(Future { 10/ 0 })
    println(result)

    val list = List(1,2,3)
    val f = (acc: Int, n: Int) => {
      println(s"acc: $acc, n: $n")
      acc + n
    }
    // val sum1 = list.foldLeft(0)(f)
    // println(sum1)

    val g = (n: Int, acc: Int) => {
      println(s"acc: $acc, n: $n")
      acc + n
    }
    val sum2 = list.foldRight(0)(g)
    println(sum2)

  }
}
