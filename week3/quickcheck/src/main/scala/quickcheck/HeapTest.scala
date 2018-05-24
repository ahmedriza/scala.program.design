package quickcheck
import scala.collection.immutable

object HeapTest {

  def main(args: Array[String]): Unit = {

    val binomailHeap = new BinomialHeap with IntHeap

    val h1: List[binomailHeap.Node] = binomailHeap.insert(60, Nil)
    val h2 = binomailHeap.insert(20, h1)
    val h3 = binomailHeap.insert(100, h2)
    val h4 = binomailHeap.insert(5, h3)
    val h5 = binomailHeap.insert(15, h4)

    println(h5)
    val min = binomailHeap.deleteMin(h5)
    println(min)
  }
}
