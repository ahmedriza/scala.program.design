import scala.collection.immutable
object ForTranslation {

  // The syntax of for is closely related to the higher order functions
  // (1) map
  // (2) flatMap
  // (3) filter

  // Firstly, all these functions can be defined in terms of for.

  def main(args: Array[String]): Unit = {
    val list = List(10, 20, 30)
    println(mapFun(list, (x: Int) => x * 10))

    println(flatMap(list, (x: Int) => List(x, x*10)))

    // Translate
    // for (b <- books; a <- b.authors if a startsWith "Bird") yield b.title
    // into higher order functions

    val books: List[Book] = ForQueries.books

    val bird1 = for {
      b <- books
      a <- b.authors
      if a startsWith "Bird"
    } yield b.title
    bird1 foreach println

    val bird2 = books.flatMap(b => for(a <- b.authors if a startsWith "Bird") yield b.title)
    bird2 foreach println

    val bird3 = books.flatMap(b =>
      for (a <- b.authors.withFilter(a => a.startsWith("Bird"))) yield b.title)
    bird3 foreach println

    val bird4 = books.flatMap(b =>
      b.authors.withFilter(a => a.startsWith("Bird")).map(_ => b.title))
    bird4 foreach println

    // Lecture 1.2 - Translation of For, shows this @ 9:53, but this is wrong
    // books.flatMap(b => b.authors.withFilter(a => a.startsWith("Bird")).map(y => y.title))

  }

  def mapFun[T, U](xs: List[T], f: T => U): List[U] =
    for (x <- xs) yield f(x)

  def flatMap[T, U](xs: List[T], f: T => Iterable[U]): List[U] =
    for (x <- xs; fx <- f(x)) yield fx

  def filter[T](xs: List[T], p: T => Boolean): List[T] =
    for (x <- xs if p(x)) yield x

  // In reality, Scala compiler expresses for expressions in terms of
  // map, flatMap and a lazy variant of filter.

}
