

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

object LazyEvaluation {


  def main(args: Array[String]): Unit = {

    // Consider the following program.
    def expr = {
      val x = { print("x"); 1 }
      lazy val y = { print("y"); 2 }
      def z = { print("z"); 3 }
      z + y + x + z + y + x
    }

    expr

    // What gets printed as a side effect of evaluating expr?
    // "xzyz"

    // Seeing lazy evaluation in action:
    //
    // Let's use the substitution model to figure out what happens to:
    //
    // streamRange(1000, 10000) filter isPrime) apply 1
    //
    // if (1000 > 10000) empty
    // else cons(1000, streamRange(1000 + 1, 10000))
    // .filter(isPrime).apply(1)
    // 

  }
}
