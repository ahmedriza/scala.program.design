

// The proposed implementation of MyStream suffers from a serious potential performance
// problem: if tail is called several times, the corresponding stream will be
// recomputed each time.
//
// This problem can be avoided by storing the result of the first evaluation of tail
// and re-using the stored result instead of recomputing tail.
//
// This optimisation is sound, since in a purely functional language an expression
// produces the same result each time it is evaluated.
//
// We call this scheme 'lazy evaluation' (as opposed to 'by-name evaluation' in the
// case where everything is recomputed, and 'strict evaluation' for normal parameters
// and val definitions)
//
// Haskell is a programming language that uses lazy evaluation by default.
// Lazy evaluation has disadvantages:
//   (i) unpredictable as to when computations happen and
//  (ii) how much space the computation uses
//
// Scala uses strict evaluation by default, but allows lazy evaluation of value
// definitions with the 'lazy val' form.

// Using lazy val for tail, MyStream.cons can be implemented more efficiently
//
// def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
//     def head = hd
//     lazy val tail = tl
//

trait LazyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: LazyStream[A]

  override def toString: String = "LazyStream(" + head + ", ?)"

  def apply(n: Int): A =
    if (n == 0) head
    else tail.apply(n - 1)

  // Other methods are implemented analogously to their List counterparts
  def filter(p: A => Boolean): LazyStream[A] =
    if (isEmpty) this
    // Note that in the line below, the recursive call
    // to filter happens as the 2nd parameter to cons() which is
    // a by-name parameter, hence the tail won't be evaluated until
    // someone actually asks for it.
    else if (p(head)) {
      LazyStream.cons(head, tail.filter(p))
    }
    else {
      tail.filter(p)
    }
}

object LazyStream {
  // Note that the tl parameter is by-name
  // Contrast this with the Cons class for Lists where the tail parameter
  // is a normal call-by-value parameter
  // That is the only thing that matters between Lists and Streams.

  def cons[T](hd: T, tl: => LazyStream[T]): LazyStream[T] = new LazyStream[T] {
    override def isEmpty: Boolean = false
    override def head: T = hd
    lazy val tail: LazyStream[T] = tl
  }

  val empty: LazyStream[Nothing] = new LazyStream[Nothing] {
    override def isEmpty: Boolean = true
    override def head = throw new NoSuchElementException("empty.head")
    override def tail = throw new NoSuchElementException("empty.tail")
  }
}


object LazyEvaluation {


  def main(args: Array[String]): Unit = {

    // Consider the following program.
    def expr = {
      val x = { print("x"); 1 }
      lazy val y = { print("y"); 2 }
      def z = { print("z"); 3 }
      z + y + x + z + y + x
    }

    // What gets printed as a side effect of evaluating expr?
    // "xzyz"


    def isPrime(n: Int): Boolean = (2 until n).forall(x => n % x != 0)

    def streamRange(lo: Int, hi: Int): LazyStream[Int] = {
      if (lo >= hi) LazyStream.empty
      else LazyStream.cons(lo, streamRange(lo + 1, hi))
    }

    println(streamRange(1, 100))

    // Seeing lazy evaluation in action:
    //
    // Let's use the substitution model to figure out what happens to:
    //
    val result = streamRange(1000, 10000).filter(isPrime).apply(1)
    println(result)

    //
    // if (1000 > 10000) empty
    // else cons(1000, streamRange(1000 + 1, 10000)).filter(isPrime).apply(1)
    //
    // Let C1 = cons(1000, streamRange(1000 + 1, 10000))
    //
    // filter:
    // (if (C1.isEmpty) C1
    // else if (isPrime(C1.head)) cons(C1.head, C1.tail.filter(isPrime))
    // else C1.tail.filter(isPrime)
    // ).apply(1)

    // (if (isPrime(C1.head)) cons(C1.head, C1.tail.filter(isPrime))
    // else C1.tail.filter(isPrime)
    // ).apply(1)

    // (if (isPrime(1000)) cons(C1.head, C1.tail.filter(isPrime))
    // else C1.tail.filter(isPrime)
    // ).apply(1)

    // (if (false) cons(C1.head, C1.tail.filter(isPrime))
    // else C1.tail.filter(isPrime)
    // ).apply(1)

    // (C1.tail.filter(isPrime)).apply(1)

    // C1.tail = streamRange(1001, 10000)
    // streamRange(1001, 10000).filter(isPrime).apply(1)
    //
    // ^ This is like what we started out with, but with 1001 instead of 1000.
    // This evaluation continues until:

    // streamRange(1009, 10000).filter(isPrime).apply(1)
    //
    // if (1009 > 10000) empty
    // else cons(1009, streamRange(1009 + 1, 10000)).filter(isPrime).apply(1)

    // Let C2 = cons(1009, streamRange(1009 + 1, 10000))
    // C2.filter(isPrime).apply(1)
    // Evaluate filter:

    // cons(1009, C2.tail.filter(isPrime)).apply(1)

    // Now we need to apply apply on this cons expression
    //
    // if (1 == 0) cons(1009, C2.tail.filter(isPrime)).head
    // else cons(1009, C2.tail.filter(isPrime)).tail.apply(0)

    // Now we evaluate tail:

    // C2.tail.filter(isPrime).apply(0)
    // streamRange(1010, 10000).filter(isPrime).apply(0)

    // This process continues until we hit the next prime number, 1013

    // streamRange(1013, 10000).filter(isPrime).apply(0)
    // streamRange expands to
    // cons(1013, streamRange(1013 + 1, 10000)).filter(isPrime).apply(0)

    // Let C3 = cons(1013, streamRange(1013 + 1, 10000))
    // C3.filter(isPrime).apply(0)
    // Evaluate filter

    // cons(1013, C3.tail.filter(isPrime)).apply(0)
    //
    // Evaluate apply
    // if (0 == 0) cons(1013, C3.tail.filter(isPrime)).head
    //
    // Evaluate head
    // 1013
    // final answer

    // Only the part of the stream necessary to compute the result has been constructed.

  }
}
