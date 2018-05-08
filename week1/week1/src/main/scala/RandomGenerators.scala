
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
    override def generate: Int = rand.nextInt()
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
        val value = if (x < 0) lo + (-x) % (hi - lo) else lo + x % (hi - lo)
        value
    }
    // for (x <- integers_) yield lo + x % (hi - lo)
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

  /**
    * @return an empty list or a non-empty list
    */
  def lists: Generator[List[Int]] = for {
    isEmpty <- booleans_
    list <- if (isEmpty) emptyLists else nonEmptyLists
  } yield list

  def emptyLists: Generator[List[Int]] = single(Nil)

  def nonEmptyLists: Generator[List[Int]] = for {
    head <- integers_
    tail <- lists
  } yield head :: tail

  //
  // Exercise
  // Can you implement a generator that creates random Tree objects?
  // Hint: a tree is either a leaf or an inner node.

  trait Tree

  case class Inner(left: Tree, right: Tree) extends  Tree

  case class Leaf(x: Int) extends Tree

  def leafNode: Generator[Tree] = for {
    x <- integers_
  } yield Leaf(x)

  def innerNode: Generator[Tree] = for {
    left <- trees
    right <- trees
  } yield Inner(left, right)

  def trees: Generator[Tree] = for {
    isLeaf <- booleans_
    tree <- if (isLeaf) leafNode else innerNode
  } yield tree

  // ------------------------------------------------------------------------------------------------------------------

  // Random Test Function

  def test[T](g: Generator[T], numTimes: Int = 100)(test: T => Boolean): Unit = {
    for (i <- 0 until numTimes) {
      val value = g.generate
      assert(test(value), "test failed for " + value)
    }
    println("passed " + numTimes + " tests")
  }

  // ------------------------------------------------------------------------------------------------------------------

  def main(args: Array[String]): Unit = {

    testBooleanGenerator()

    val list = List(1,2,3)
    def flatMap(xs: List[Int], f: Int => List[Int]): List[Int] = for (x <- xs; fx <- f(x)) yield fx
    println(flatMap(list, (x: Int) => List(x * 2)))

    val one: Generator[String] = oneOf("Red", "Blue", "Yellow")
    println(one.generate)

    val choice: Generator[Int] = choose(0, 3)
    println(choice.generate)

    for (i <- 1 to 5) {
      val ne = lists.generate
      println(ne)
    }

    for (i <- 1 to 5) {
      val tr = trees.generate
      println(tr)
    }

    // Example usage of test function
    test(pairs_(lists, lists)) {
      // Note (xs ++ ys).length > xs.length will fail on two empty lists or a non-empty list and an empty list
      case (xs, ys) => (xs ++ ys).length >= xs.length
    }
  }
}
