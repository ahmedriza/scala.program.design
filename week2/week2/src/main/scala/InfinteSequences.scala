
object InfinteSequences {

  // Infinite stream.
  def from(n: Int): Stream[Int] = n #:: from(n + 1)

  // Sieve of Eratosthenes for calculating prime numbers
  // ----------------------------------------------------
  // The idea is as follows
  // (1) Start with all integers from 2, the first prime number
  // (2) Eliminate all multiples of 2
  // (3) The first element of the resulting list is 3, a prime number.
  // (4) Eliminate all multiples of 3
  // (5) Iterate forever. At each step, the first number in the list is a prime number
  //     and we eliminate all its multiples.

  def sieve(stream: Stream[Int]): Stream[Int] = {
     stream.head #:: sieve(stream.tail.filter(_ % stream.head != 0))
  }

  def improve(guess: Double): Double = (guess + 2.0 / guess) / 2

  def guesses: LazyStream[Double] = LazyStream.cons(1, guesses map improve)

  def isGoodEnough(guess: Double, x: Double): Boolean = {
    math.abs((guess * guess - x) / x) < 0.000001
  }

  def sqrtStream(x: Double): LazyStream[Double] = {
    def improve(guess: Double): Double = (guess + x / guess) / 2
    def guesses: LazyStream[Double] = LazyStream.cons(1, guesses map improve)
    guesses
  }

  def main(args: Array[String]): Unit = {

    // Natual numbers
    val nats = from(0)
    println(nats)

    val multiplesOfFour = nats map (_ * 4)
    println((multiplesOfFour take 5).toList)

    val primes = sieve(from(2))
    println("primes: " + (primes take 10).toList)

    // println(guesses.take(10).toList)
    println(guesses.take(5))

    val result = sqrtStream(2.0) filter (isGoodEnough(_, 2.0))
    println(result)
  }

}
