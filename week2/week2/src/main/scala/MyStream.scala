
import scala.collection.AbstractSeq

// Stream Implementation
// As for Lists, all other methods can be defined in terms of these fundamental ones.

trait MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  // Other methods are implemented analogously to their List counterparts
  def filter(p: A => Boolean): MyStream[A] =
    if (isEmpty) this
    // Note that in the line below, the recursive call
    // to filter happens as the 2nd parameter to cons() which is
    // a by-name parameter, hence the tail won't be evaluated until
    // someone actually asks for it.
    else if (p(head)) MyStream.cons(head, tail.filter(p))
    else tail.filter(p)
}

object MyStream {

  // Note that the tl parameter is by-name
  // Contrast this with the Cons class for Lists where the tail parameter
  // is a normal call-by-value parameter
  // That is the only thing that matters between Lists and Streams.

  def cons[T](hd: T, tl: => MyStream[T]): MyStream[T] = new MyStream[T] {

    override def isEmpty: Boolean = false

    override def head: T = hd

    override def tail: MyStream[T] = tl
  }

  val empty: MyStream[Nothing] = new MyStream[Nothing] {

    override def isEmpty: Boolean = true

    override def head = throw new NoSuchElementException("empty.head")

    override def tail = throw new NoSuchElementException("empty.tail")
  }
}