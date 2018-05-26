package quickcheck

import common._
import org.scalacheck._
import Arbitrary._
import Gen._
import Prop.forAll
import org.scalacheck.Prop.BooleanOperators

import scala.annotation.tailrec

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = {

    def loop(h: H, size: Int): Gen[H] = {
      if (size == 0) h
      else {
        for {
          v <- arbitrary[Int]
          hg <- loop(insert(v, h), size - 1)
        } yield hg
      }
    }

    val sized: Gen[H] = Gen.sized { size =>
      loop(empty, size)
    }

    sized
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

  property("insert a,b,c into H, findMin = min(a, b, c)") = forAll { (a: Int, b: Int, c: Int) =>
    val h1 = insert(a, empty)
    val h2 = insert(b, h1)
    val h3 = insert(c, h2)

    val expectedMin = math.min(math.min(a, b), c)
    val expectedMax = math.max(math.max(a, b), c)

    val min = findMin(h3)
    min == expectedMin

    val h4 = deleteMin(h3) // delete the second smallest element

    val h5 = deleteMin(h4) // now this should have the largest element as the only element left.

    findMin(h5) == expectedMax
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

  // If we insert an element into a heap with a single element, then the new element
  // should be linked to the existing element, with the smaller one as the root
  property("insert a into H with a single node") = forAll { (a: Int, b: Int) =>

    val minElement = math.min(a, b)
    val maxElement = math.max(a, b)
    val h1 = insert(a, empty) // element a is at the root
    val h2 = insert(b, h1)

    // we should get the two elements in ascending order
    val m1 = findMin(h2)
    val h3 = deleteMin(h2)
    val m2 = findMin(h3)

    m1 == minElement
    m2 == maxElement
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
