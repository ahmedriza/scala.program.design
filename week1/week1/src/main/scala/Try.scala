import scala.util.control.NonFatal

// In the laster parts of this course we will need a type name Try.
//
// Try resembles Option, but instead of Some/None there is a Success case
// with a value and a Failure case that contains an exception


abstract class Try[+T] {

  def flatMap[U](f: T => Try[U]): Try[U] = ???

  def map[U](f: T => U): Try[U] = ???

}

case class Success[T](x: T) extends Try[T]

case class Failure(ex: Throwable) extends Try[Nothing]

// Try is used to pass results of computations that can fail with an exception between threads and computers
// You can wrap up an arbitrary computation in a Try.
//
// Try(expr) // gives Success(someValue) or Failure(someException)

// Here's an implementation of Try:

object Try {

  // Note expr is a call-by-name parameter, since we need to evaluate it within this function
  def apply[T](expr: => T): Try[T] =
    try Success(expr) catch {
      case NonFatal(ex) => Failure(ex)
    }

  // Just like with Option, Try-valued computations can be composed in for expressions.
  //
  // for {
  //   x <- computeX
  //   y <- computeY
  // } yield f(x, y)

  // If computeX and computeY succeed, with results Success(x) and Success(y), this will return
  // Success(f(x, y)).
  //
  // If either computation fails with an exception ex, this will return Failure(ex).

  def main(args: Array[String]): Unit = {
    val result: Try[Int] = Try(10/0)
    println(result)
  }
}