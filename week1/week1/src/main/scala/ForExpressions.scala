import scala.collection.immutable

object ForExpressions {

  def main(args: Array[String]): Unit = {

    // Rule (1)
    // A simple for expression
    // for (x <- e1) yield 2
    // translates to
    // e1.map(x => e2)
    val list = List(1,2,3,4,5,6)
    println(for (x <- list) yield x*2)
    println(list.map(_ * 2))

    println("----------------------------------------------------")

    // Rule (2)
    // for (x <- e1 if f; s) yield e2
    // where f is a filter and s is a (potentially empty) sequence of generators and filters, is translated to
    // for (x <- e1.withFilter(x => f); s) yield e2
    val list2 = List(10, 20, 30)
    println(
      for {
        x <- list
        if x % 2 == 0
        y <- list2
      } yield x * 3
    )
    println(for (x <- list if x % 2 == 0; y <- list2) yield x * 3)
    println(for(x <- list.withFilter(x => x % 2 == 0); y <- list2) yield x * 3)

    val zzz = list.withFilter(x => x % 2 == 0).flatMap(x =>
      list2.map(y => x * 3))
    println(zzz)

    println("----------------------------------------------------")

    // Rule (3)
    // for (x <- e1; y <- e2; s) yield e3
    // e1.flatMap(x => for (y <- e2; s) yield e3)
    // and the translation continues with the new expression
    println(for (x <- list; y <- list2) yield x * y)

    println(list.flatMap(x => list2.map(y =>  x * y)))

    println(list.flatMap(x => for (y <- list2) yield x * y))

    println("----------------------------------------------------")

    val list3 = List(1, 2, 3)
    val list4 = List(11, 12, 13)
    val xyz1 = for {
      a <- list3
      b <- list4
      if b % 2 == 0
    } yield a * b
    println(xyz1)

    val xyz2 = list3.flatMap(a =>
      list4.withFilter(x => x % 2 == 0).map(b => a * b))
    println(xyz2)

    println("----------------------------------------------------")

    // Pattern Matching in For
    // ----------------------------------------------

    // pat <- expr
    //
    // is translated to
    //
    // x <- expr withFilter {
    //         case pat => true
    //         case _   => false
    //      } map {
    //        case pat => x
    //      }

    val data = List[Json](Json.toJson)

    val result: immutable.Seq[(Json, Json)] = for {
      JObj(bindings) <- data
      JSeq(phones) = bindings("phoneNumbers")
      JObj(phone) <- phones
      JStr(digits) = phone("number")
      if digits startsWith "212"
    } yield (bindings("firstName"), bindings("lastName"))

    println(result)

    val bindings = data withFilter {
      case JObj(_) => true
      case _       => false
    } map {
      case JObj(b) => b
    }

    println(bindings)

    // Exercise
    //
    val N = 10
    val tmp1 = for {
      x <- 2 to N
      y <- 2 to x
      if x % y == 0
    } yield (x, y)

    // The expression above expands to
    val tmp2 = (2 to N ) flatMap (x =>
      (2 to x) withFilter (y =>
        x % y == 0) map (y => (x, y)))

    println(tmp1)
    println(tmp2) 
  }
}
