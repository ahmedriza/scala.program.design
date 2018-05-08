
trait GeneratorV1[+T] {
  def generate: T
}

object RandomGenerators {

  val integers: GeneratorV1[Int] = new GeneratorV1[Int] {
    val rand = new java.util.Random()
    override def generate: Int = rand.nextInt()
  }

  val booleans: GeneratorV1[Boolean] = new GeneratorV1[Boolean] {
    override def generate: Boolean = integers.generate > 0
  }

  val pairs: GeneratorV1[(Int, Int)] = new GeneratorV1[(Int, Int)] {
    override def generate: (Int, Int) = (integers.generate, integers.generate)
  }

  def testBooleanGenerator(): Unit = {
    val N = 1000000
    val bs = (1 to N).foldLeft(List[Boolean]())( (acc, _) => booleans.generate :: acc)
    val numberOfFalse = bs.count(p => !p)
    // Probability of a false:
    println(1.0 * numberOfFalse / N)
  }

  //
  // What we'd like to do
  //
  // (1)
  // val booleans = for (x <- integers) yield x > 0
  //
  // (2)
  // def pairs[T,U](t: Generator[T}, u: Generator[U]) = for {
  //   x <- t
  //   y <- u
  // } yield (x, y)
  //
  // What would the compiler expand these expressions to?
  // (1)
  // integers.map(x => x > 0)
  //
  // (2)
  // t.flatMap(x => u.map(y => (x, y)))
  //

  // Here's a more convenient version of Generator:

  trait Generator[+T] {
    self => // an alias for "this"

    def generate: T

    def map[S](f: T => S): Generator[S] = new Generator[S] {
      // note that we cannot use f(this.generate), since "this" refers to this anonymous class and will call
      // the generate method here recursively.
      // Instead we create an alias for "this" of the Generator trait.
      // Alternatively, we could have used: f(Generator.this.generate) as well
      override def generate: S = f(self.generate)
    }

    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
      override def generate: S = f(self.generate).generate
    }
  }

  val integers_ : Generator[Int] = new Generator[Int] {
    val rand = new java.util.Random()
    override def generate: Int = rand.nextInt(Integer.MAX_VALUE)
  }

  // Now we can write
  // val booleans = for (x <- integers) yield x > 0
  val booleans_ : Generator[Boolean] = integers_.map(x => x > 0)

  def pairs_[T, U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = t.flatMap(x => u.map(y => (x, y)))
  // The above expands to (by expanding map):
  def pairs__[T,U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = t.flatMap {
    x => new Generator[(T,U)] {
      override def generate: (T, U) = (x, u.generate)
    }
  }
  // If we expand flatMap, we get:
  def pairs___[T,U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = new Generator[(T, U)] {
    override def generate: (T, U) = new Generator[(T, U)] {
      override def generate: (T, U) = (t.generate, u.generate)
    }.generate
  }
  // We can simplify this:
  def pairs____[T,U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = new Generator[(T, U)] {
    override def generate: (T, U) = (t.generate, u.generate)
  }

  // Some useful building blocks

  // A single generator: always gives back the same value
  def single[T](x: T): Generator[T] = new Generator[T] {
    override def generate: T = x
  }

  /**
    * Generate a random integer between lo and hi
    * @param lo lower range
    * @param hi higher range
    * @return a random integer in the range lo, hi
    */
  def choose(lo: Int, hi: Int): Generator[Int] = {
    integers_.map {
      x =>
        val value = lo + x % (hi - lo)
        value
    }
    /*
    for {
      x <- integers_
    } yield lo + x % (hi - lo)
    */
  }

  /**
    * Pick a random value from given choices
    * @param xs choices of values to choose from
    * @tparam T type of values
    * @return an arbitrary value chosen from xs
    */
  def oneOf[T](xs: T*): Generator[T] = for {
    idx <- choose(0, xs.length)
  } yield xs(idx)

  //
  // With these building blocks, how would we generate a random list?

  def lists: Generator[List[Int]] = ???



  def main(args: Array[String]): Unit = {

    testBooleanGenerator()

    val list = List(1,2,3)
    def flatMap(xs: List[Int], f: Int => List[Int]): List[Int] = for (x <- xs; fx <- f(x)) yield fx
    println(flatMap(list, (x: Int) => List(x * 2)))

    val one: Generator[String] = oneOf("Red", "Blue", "Yellow")
    println(one.generate)

    val choice: Generator[Int] = choose(0, 3)
    println(choice.generate)
  }
}
