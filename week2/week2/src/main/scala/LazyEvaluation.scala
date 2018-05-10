

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
    else if (p(head)) LazyStream.cons(head, tail.filter(p))
    else tail.filter(p)
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
    val result = (streamRange(1000, 10000) filter isPrime) apply 1
    println(result)

    //
    // if (1000 > 10000) empty
    // else cons(1000, streamRange(1000 + 1, 10000))
    // .filter(isPrime).apply(1)
    //
  }
}
