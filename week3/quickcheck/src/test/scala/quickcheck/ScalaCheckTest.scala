package quickcheck

import org.scalacheck.{Arbitrary, Gen, Prop}

object ScalaCheckTest {

  def main(args: Array[String]): Unit = {

    import org.scalacheck.Prop.forAll
    import org.scalacheck.Prop.BooleanOperators

    val propConcatLists: Prop = forAll { (l1: List[Int], l2: List[Int]) => l1.size + l2.size == (l1 ::: l2).size }

    val propReverseList = forAll { l: List[String] => l.reverse.reverse == l }

    val propConcatString = forAll { (s1: String, s2: String) => (s1 + s2).endsWith(s2) }

    val smallInteger = Gen.choose(0, 100)
    val propSmallInteger = forAll(smallInteger) {  n: Int => n >= 0 && n <= 100 }

    val propMakeList = forAll { n: Int =>
      (n >= 0 && n < 1000) ==> (List.fill(n)("").length == n)
    }

    propConcatLists.check
    propReverseList.check
    propConcatString.check
    propSmallInteger.check
    propMakeList.check

    // Generators

    val myGen: Gen[(Int, Int)] = for {
      n <- Gen.choose(10, 20)
      m <- Gen.choose(2*n, 500)
    } yield (n, m)

    println(myGen.sample)

    val vowel: Gen[Char] = Gen.oneOf('A', 'E', 'I', 'O', 'U', 'Y')
    println(vowel.sample)

    // ----------------
    // Generating case classes

    sealed abstract class Tree

    case class Node(left: Tree, right: Tree, v: Int) extends Tree
    case object Leaf extends Tree

    import Gen._
    import org.scalacheck.Arbitrary.arbitrary

    val genLeaf = const(Leaf)

    lazy val genNode = for {
      v <- arbitrary[Int]
      left <- genTree
      right <- genTree
    } yield Node(left, right, v)

    def genTree: Gen[Tree] = oneOf(genLeaf, genNode)

    println(genTree.sample)


    // Arbitraty custom generator

    abstract sealed class MyTree[T] {
      def merge(t: MyTree[T]) = Internal(List(this, t))

      def size: Int = this match {
        case MyLeaf(_) => 1
        case Internal(children) => (children foldRight 0) (_.size + _)
      }
    }

    case class Internal[T](children: Seq[MyTree[T]]) extends MyTree[T]

    case class MyLeaf[T](elem: T) extends MyTree[T]

    // When you specify an implicit generator for your type MyTree[T], you also have to assume that there exists
    // an implicit generator for the type T.  You do this by specifying an implicit parameter of type Arbitrary[T],
    // so you can use the generator arbitrary[T].

    implicit def arbTree[T](implicit a: Arbitrary[T]): Arbitrary[MyTree[T]] = Arbitrary {

      val genLeaf = for(e <- Arbitrary.arbitrary[T]) yield MyLeaf(e)

      def genInternal(sz: Int): Gen[MyTree[T]] = for {
        n <- Gen.choose(sz / 3, sz / 2)
        c <- Gen.listOfN(n, sizedTree(sz / 2))
      } yield Internal(c)

      def sizedTree(sz: Int): Gen[MyTree[T]] =
        if (sz <= 0 ) genLeaf
        else Gen.frequency((1, genLeaf), (3, genInternal(sz)))

      Gen.sized(sz => sizedTree(sz))
    }

    val propMergeTree = forAll {
      (t1: MyTree[Int], t2: MyTree[Int]) => t1.size + t2.size == t1.merge(t2).size
    }

    // propMergeTree.check

    // ----------------

    lazy val genMap: Gen[Map[Int, Int]] = oneOf(
      const(Map.empty[Int, Int]),
      for {
        k <- arbitrary[Int]
        v <- arbitrary[Int]
        m <- oneOf(const(Map.empty[Int, Int]), genMap)
      } yield m.updated(k, v)
    )

    println(genMap.sample)

    // ----------------
  }
}
