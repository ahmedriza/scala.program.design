package quickcheck
import scala.collection.immutable

object HeapTest {


  def main(args: Array[String]): Unit = {

    val binomailHeap = new BinomialHeap with IntHeap

    import binomailHeap._

    val h1: List[Node] = insert(60, Nil)
    println(h1)
    val lh1 = List(Node(60,0,List()))

    val h2 = insert(20, h1)
    println(h2)
    val lh2 =  List(Node(20,1,  List(Node(60,0,List()))))

    val h3 = insert(100, h2)
    println(h3)
    val lh3 = List(Node(100,0, List()), Node(20,1, List(Node(60,0,List()))))

    // + Heap.insert a into empty H, deleteMin == empty H: OK, passed 100 tests.
    // List(-1, -2147483648)
    // ! Heap.sorted sequence: Falsified after 0 passed tests.
    // > ARG_0: List(Node(-2147483648,1,List(Node(-1,0,List()))))

    def f(h: H, acc: List[Int]): List[Int] = {
      if (isEmpty(h)) {
        acc
      } else {
        val min = findMin(h)
        f(deleteMin(h), min :: acc)
      }
    }

    val h6 = insert(-1, empty)
    val h7 = insert(-2147483648, h6)
    println(s"h7: $h7")
    val sortedList = f(h7, List()).reverse

    println(sortedList)
  }
}
