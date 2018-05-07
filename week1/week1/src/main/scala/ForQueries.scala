import scala.collection.immutable

case class Book(title: String, authors: List[String])

object ForQueries {

  // Lets design a library of books

  val books = List(
    Book(title = "Structure and Interpretation of Computer Programs",
      authors = List("Abelson, Harald", "Sussman, Gerald J.")),

    Book(title = "Introduction to Functional Programming",
      authors = List("Bird, Richard", "Wadler, Phil")),

    Book(title = "Effective Java", authors = List("Bloch, Joshua")),

    Book(title = "Java Puzzlers", authors = List("Bloch, Joshua", "Gafter, Neal")),

    Book(title = "Programming in Scala",
      authors = List("Odersky, Martin", "Spoon, Lex", "Venners, Bill"))
  )

  def main(args: Array[String]): Unit = {

    // Find the titles of books whose author's name is "Bird"
    val birds = for {
      book <- books
      a <- book.authors
      if a.startsWith("Bird")
    } yield book.title
    println(birds)
    println("----------------------------------------")

    // Find all books that have the workd "Program" in the title
    val progBooks = for {
      book <- books
      if book.title.indexOf("Program") >= 0
    } yield book
    progBooks foreach println
    println("----------------------------------------")

    // Find the names of all authors who have written at least two books
    // present in the database
    val authors = for {
      b1 <- books
      b2 <- books
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1

    authors foreach println
    println("----------------------------------------")

    // We get the "Bloch, Joshua" twice, since we get a pair of books from the two generators
    // each time with a different order
    // ("Effective Java", "Java Puzzlers")
    // ("Java Puzzlers", "Effective Java")

    // How can we avoid it?
    val authors2 = for {
      b1 <- books
      b2 <- books
      if b1.title < b2.title
      a1 <- b1.authors
      a2 <- b2.authors
      if a1 == a2
    } yield a1

    authors2 foreach println
    println("----------------------------------------")

    // If we have three books with the same author, however, this doesn't work
    // and we'll get the author 3 times:


    // Add another book by Bloch, Joshua
    val books2 = Book(title = "Java is Dangerous", authors = List("Bloch, Joshua")) :: books
    val authors3: Seq[String] = books2.flatMap(b1 =>
      books2.withFilter {
        b => b1.title < b.title
      }.flatMap(b2 => {
        println(s"b1: ${b1.title}, b2: ${b2.title}")
        b1.authors.flatMap(a1 => {
          b2.authors.withFilter(a => a == a1).map(a => a1)
        })
      })
    )
    authors3 foreach println
    println("----------------------------------------")
    // How do we fix that?
    // One possible solution
    authors3.distinct foreach println
    println("----------------------------------------")

    // We can also eliminate this by designing our library as a Set rather than a List

    val books3 = books2.toSet
    val authors4: Set[String] = books3.flatMap(b1 =>
      books3.withFilter {
        b => b1.title < b.title
      }.flatMap(b2 => {
        println(s"b1: ${b1.title}, b2: ${b2.title}")
        b1.authors.flatMap(a1 => {
          b2.authors.withFilter(a => a == a1).map(a => a1)
        })
      })
    )

    authors4 foreach println
    println("----------------------------------------")

  }


}
