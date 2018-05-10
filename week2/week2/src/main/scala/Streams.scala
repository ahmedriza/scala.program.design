import scala.collection.immutable

object Streams {

  def isPrime(n: Int): Boolean = (2 until n).forall(i => n % i != 0)

  def secondPrime(from: Int, to: Int): Int = nthPrime(from, to, 2)

  def nthPrime(from: Int, to: Int, n: Int): Int =
    if (from >= to) throw new Error("no prime")
    else if (isPrime(from)) {
      if (n == 1) from
      else nthPrime(from + 1, to, n - 1)
    } else {
      nthPrime(from + 1, to, n)
    }

  // -------------------------------------------------------------------------------------------

  def main(args: Array[String]): Unit = {

    // Find the second prime number between 1000 and 10000
    val second = ((1000 to 10000) filter isPrime)(1)
    println(second)
    println(secondPrime(1000, 10000))

    // This is much shorter than the recursive alternative given by secondPrime
    // However, this is still very inefficient, since we construct all prime numbers
    // between 1000 and 10000 in a list, but only ever looks at the first two elements
    // of that list.

    // However, we can make the short-code efficient by using a trick:
    //
    // Avoid computing the tail of a sequence until it is needed for the
    // evaluation result (which might be never)

    // This idea is implemented in a new class, Stream.
    // Streams are similar to lists, but their tail is evaluated only on demand.

    val xs: Seq[Int] = Stream.cons(1, Stream.cons(2, Stream.empty))

    println(xs)
    println(xs.tail)

    val xss = Stream(1, 2, 3)
    println(xss)

    println((1 to 1000).toStream)

    // Stream Ranges
    // Let's try to write a function that returns (lo until hi).toStream
    // directly

    def streamRange(lo: Int, hi: Int): Stream[Int] = {
      if (lo >= hi) Stream.empty
      else Stream.cons(lo, streamRange(lo + 1, hi))
    }

    // Compare to the same function that produces a list range
    def listRange(lo: Int, hi: Int): List[Int] = {
      if (lo >= hi) Nil
      else lo :: listRange(lo + 1, hi)
    }

    // Methods on Streams
    val second_ = ((1000 to 10000).toStream filter isPrime)(1)
    println(second_)

    // Note that
    // x #:: xs == Stream.cons(x, xs)
    val stream1 = 1 #:: Stream.empty
    val stream2 = 10 #:: stream1
    println(stream2)

  }
}
