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
    val lh2 =
      List(
        Node(20,1,
          List(
            Node(60,0,List())
          )
        )
      )

    val h3 = insert(100, h2)
    println(h3)
    val lh3 =
      List(
        Node(100,0, List()),
        Node(20,1,
          List(
            Node(60,0,List())
          )
        )
      )

    val h4 = insert(5, h3)
    println(h4)
    val lh4 =
      List(
        Node(5,2,
          List(
            Node(20,1, List(Node(60,0,List()))),
            Node(100,0,List())
          )
        )
      )

    val h5 = insert(15, h4)
    println(h5)
    val lh5 =
      List(
        Node(15,0,List()),
        Node(5,2,
          List(
            Node(20,1,List(Node(60,0,List()))),
            Node(100,0,List())
          )
        )
      )

    // val min = binomailHeap.deleteMin(h5)
    // println(min)

    import org.scalacheck.Prop.forAll
    val propConcatLists = forAll {
      (l1: List[Int], l2: List[Int]) => l1.size + l2.size == (l1 ::: l2).size
    }
    propConcatLists.check
  }
}
