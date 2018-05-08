import scala.collection.immutable

// Data structures with map and flatMap seem to be quite common.
//
// In fact, there's a name that describes this class of data structures together with some
// algebraic laws that they should have.
//
// They are called Monads. What is a Monad?
// ----------------------------------------
//
// A Monad M is a parametric type M[T] with two operations, flatMap and unit, that have to satisfy some laws.
//         In the literature, flatMap is more commonly called 'bind'.
//
trait M[T] {
  def flatMap[U](f: T => M[U]): M[U]
}

// def unit[T](x: T): M[T]

// If they also define 'withFilter', they are called "Monads with zero"

// Example of Monads
//
// (1) List is a monad with unit(x) = List(x)
// (2) Set is a monad with unit(x) = Set(x)
// (3) Option is a monad with unit(x) = Some(x)
// (4) Generator is a monad with unit(x) = single(x)

// flatMap is an operation on each of these types, whereas 'unit' in Scala is different for each Monad.
//
// What about map?
// map can be defined for every Monad as a combination of flatMap and unit:
//
// m map f = m flatMap (x => unit(f(x)))
//         = m flatMap (f andThen unit)
//
// Note: In Scala we do not have a 'unit' we can call (since every Monad has a different expression that gives
//       a unit value), so 'map' is also a primitive that is defined for every Monad.
//
// Monad Laws
// ----------
// To qualify as a Monad, a type has to satisfy three laws:
// (1) Associativity
//     m flatMap f flatMap g == m flatMap (x => f(x) flatMap g)
//     This is actually:
//     (m flatMap f) flatMap g == m flatMap (x => f(x) flatMap g)
//
// (2) Left unit
//     unit(x) flatMap f == f(x)
//
// (3) Right unit
//     m flatMap unit == m

//
// Checking Monad Laws
// ---------------------
// Let's check the Monad laws for Option
// Here's flatMap for Option:

abstract class MyOption[+T] {
  def flatMap[U](f: T => MyOption[U]): MyOption[U] = this match {
    case MySome(x) => f(x)
    case MyNone => MyNone
  }
}

case class MySome[+T](value: T) extends MyOption[T]
case object MyNone extends MyOption[Nothing]

// (1)
// Checking the Left Unit Law
// Need to show: MySome(x) flatMap f == f(x)
//
// MySome(x) flatMap f
// == MySome(x) match {
//   case MySome(x) => f(x)
//   case MyNone    => MyNone
// }
// == f(x)
//
// (2)
// Checking the Right Unit Law
// Need to show: opt flatMap MySome = opt
//
// opt flatMap MySome
// == opt match {
//  case MySome(x) => MySome(x)
//  case MyNone    => MyNone
// }
// == opt (since in each of the case branches, we return the thing that we started with).
//
// (3)
// Check the Associative Law
// Need to show: opt flatMap f flatMap g == opt flatMap (x => f(x) flatMap g)
//
// (opt flatMap f) flatMap g
// We expand the LHS in parenthesis immediately followed by the RHS:
// == opt match { case MySome(x) => f(x)
//                case MyNone => MyNone
//              }                                                   // (opt flatMap f)
//        match { case MySome(y) => g(y)
//                case MyNone => MyNone
//              }                                                   // ... flatMap g
//
// Expand the (flatMap g) into the LHS:
//
// == opt match {
//        case MySome(x) =>
//          f(x) match { case MySome(y) => g(y) case MyNone => MyNone }
//        case MyNone =>
//          MyNone match { case MySome(y) => g(y) case MyNone => MyNone }
//   }
//
// Simplify the None case:

// == opt match {
//        case MySome(x) =>
//          f(x) match { case MySome(y) => g(y) case MyNone => MyNone }
//        case MyNone => MyNone
//    }

// Simplify the first case
// == opt match {
//        case  MySome(x) => f(x) flatMap g
//        case MyNone => MyNone
//    }

// == opt flatMap (x => f(x) flatMap g)
//
// Hence associativity is proved.

//
// Significance of the Laws for For-Expressions
// ------------------------------------------------
// We have seen that Monad typed expressions are typically written as for expressions.
// What is the significance of the laws with respect to this?
//
// (1) Associativity says essentially that one can "inline" nested for expressions:
//
// for (y <- for (x <- m; y <- f(x)) yield y
//     z <- g(y)) yield z
// ==
// for (x <- m;
//      y <- f(x)
//      z <- g(y)) yield z
//
// (2) Right unit says:
// for (x <- m) yield x
// == m
//
// (3) Left unit does not have an analogue for for-expressions

object Monads {

  def main(args: Array[String]): Unit = {

    def map(xs: List[Int], f: Int => List[Int]): List[Int] = xs.flatMap(x => f(x))
    val list: List[Int] = List(1,2,3)
    println(map(list, (x: Int) => List(x * 2)))

    val list2: List[Int] = for (x <- list) yield x  // Demonstration of Right unit law
    assert(list == list2)

  }
}
