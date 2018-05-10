
object StreamsTest {

  // Consider this modification of streamRange
  def streamRange(lo: Int, hi: Int): Stream[Int] = {
    print(lo + " ")
    if (lo >= hi) Stream.empty
    else Stream.cons(lo, streamRange(lo + 1, hi))
  }

  def main(args: Array[String]): Unit = {

    // When you write
    // streamRange(1, 10).take(3).toList
    // what gets printed?

    streamRange(1, 10).take(3).toList

    // 1 2 3
    // Because, toList will force the spine of the stream


  }
}
