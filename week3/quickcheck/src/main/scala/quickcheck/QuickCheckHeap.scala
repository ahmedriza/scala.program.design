package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop.forAll
import org.scalacheck.Prop.BooleanOperators

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = {

    val tmp: Gen[H] = Gen.oneOf(
      const(empty),
      for {
        v1 <- arbitrary[Int]
        v2 <- arbitrary[Int]
        h1 <- insert(v1, empty)
        h2 <- insert(v2, h1)
      } yield h2
    )

    tmp
  }

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("insert m (m = min of H) into H, findMin == m") = forAll { h: H =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("insert a into empty H, findMin == a") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("insert a, b, c into empyt H, findMin = min(a, b, c)") = forAll { (a: Int, b: Int, c: Int) =>
    val h1 = insert(a, empty)
    val h2 = insert(b, h1)
    val h3 = insert(c, h2)

    val min = findMin(h3)
    val expectedMin = math.min(math.min(a, b), c)
    min == expectedMin
  }

  // If you insert any two elements into an empty heap, finding the minimum of
  // the resulting heap should get the smallest of the two elements back
  property("insert a, b into empty H, findMin == min(a,b)") = forAll {
    (a: Int, b: Int) =>
      val min = math.min(a, b)
      val h1 = insert(a, empty)
      val h2 = insert(b, h1)
      findMin(h2) == min
  }

  // If you insert an element into an empty heap, then delete the minimum, the resulting heap
  // should be empty
  property("insert a into empty H, deleteMin == empty H") = forAll { a: Int =>
    val h = insert(a, empty)
    deleteMin(h) == empty
  }

  // Given any heap, you should get a sorted sequence of elements when continually finding
  // and deleting minima. (Hint: recursion and helper functions)
  property("sorted sequence") = forAll { h: H =>
    val sortedSequence = f(h, List()).reverse
    sortedSequence.sorted == sortedSequence
  }

  def f(h: H, acc: List[Int]): List[Int] = {
    if (isEmpty(h)) {
      acc
    } else {
      val min = findMin(h)
      f(deleteMin(h), min :: acc)
    }
  }

  // Finding the minimum of the melding of any two heaps should return
  // a minimum of one or the other.
  property("meld") = forAll { (h1: H, h2: H) =>
    val m = meld(h1, h2)
    val min1 = if (isEmpty(h1)) 0 else findMin(h1)
    val min2 = if (isEmpty(h2)) 0 else findMin(h2)

    val mmin = if (isEmpty(m)) 0 else findMin(m)
    mmin == min1 || mmin == min2
  }

}
