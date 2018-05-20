
object Iterate {

  def iterate(n: Int, f: Int => Int, x: Int): Int = {
    println(s"n: n, x: $x")
    if (n == 0) {
      x
    } else {
      val res = iterate(n - 1, f, f(x))
      println(s"n: $n, x: $x, res: $res")
      res
    }
  }

  def square(x: Int): Int = x * x

  def main(args: Array[String]): Unit = {

    val result = iterate(2, square, 3)
    println(result)
  }
}
