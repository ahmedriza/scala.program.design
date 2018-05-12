
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

  def map[B](f: A => B): LazyStream[B] =
    if (isEmpty) LazyStream.empty
    else LazyStream.cons(f(head), tail.map(f))

  def take(n: Int): LazyStream[A] =
    if (n <= 0 || isEmpty) LazyStream.empty
    else LazyStream.cons(head, tail.take(n - 1))
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
