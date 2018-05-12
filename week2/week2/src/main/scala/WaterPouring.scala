import scala.collection.immutable

/**
  * Water Pouring Problem
  *
  * Represent Glass as Int
  * Glass : Int
  *
  * Represent the state of the glass, i.e. amount of of water by a vector
  * State: Vector[Int] where the index represents the glass number
  *
  * Moves:
  * Empty(glass)
  * Fill(glass)
  * Pour(from, to)
  *
  * Example: Two glasses with capacity 4 and 9.  Starting states are both are empty
  * -------------------------------------------------------------------------------
  * Glass: 2
  * State: Vector(0, 0)
  *
  * How could this evolve?
  *
  * (i) We could fill glass 0, or
  * (ii) We could fill glass 1
  * (iii) We could pour from 0 to 1
  * (iv) We could pour from 1 to 0
  * (v) We could now empty glass 1
  *
  *              Pour(0, 1)
  * +------>[4|0]-------------> [0\4]
  * |       /
  * |      / Fill 0
  * |   [0|0]
  * |      \ Fill 1
  * |       \
  * |        \    Pour(1, 0)           Empty glass 1
  * |      [0|9] -------------> [4|5] ---------------+
  * |                                                |
  * +------------------------------------------------+
  *
  * How do we generate moves to find the right solution, say target capacity of 6?
  */

class WaterPouring(capacity: Vector[Int]) {

  import WaterPouring._

  val initialState: State = capacity.map(x => 0)

  // Moves
  trait Move {
    def change(state: State): State
  }

  case class Empty(glass: Int) extends Move {
    override def change(state: State): State = state.updated(glass, 0)
  }

  case class Fill(glass: Int) extends Move {
    override def change(state: State): State = state.updated(glass, 1)
  }

  case class Pour(from: Int, to: Int) extends Move {
    override def change(state: State): State = {
      val available = capacity(to) - state(to)
      val transfer = math.min(available, state(from))
      // println(s"state: $state, move: $this, available: $available, transfer: $transfer")
      val s1 = state.updated(from, state(from) - transfer)
      s1.updated(to, state(to) + transfer)
    }
  }

  val glasses: Range = capacity.indices

  val moves: immutable.IndexedSeq[Move] =
    (for (g <- glasses) yield Empty(g)) ++
      (for (g <- glasses) yield Fill(g)) ++
      (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))

  // Paths
  // Path is a history of moves
  
}

object WaterPouring {

  // States
  type State = Vector[Int]


  def main(args: Array[String]): Unit = {
    val waterPouring = new WaterPouring(Vector(4, 7))

    println("glasses: " + waterPouring.glasses.toList)
    println("moves: " + waterPouring.moves)
    println("--------------------------------------------")

    val s1 = waterPouring.Pour(1, 0).change(Vector(0, 7))
    println(s1)
    println("--------------------------------------------")

    val s2 = waterPouring.Pour(1, 0).change(Vector(1, 5))
    println(s2)
    println("--------------------------------------------")

    val s3 = waterPouring.Pour(1, 0).change(Vector(4, 5))
    println(s3)
    println("--------------------------------------------")

    val s4 = waterPouring.Pour(0, 1).change(Vector(2, 7))
    println(s4)

    println("--------------------------------------------")
    val s5 = waterPouring.Pour(0, 1).change(Vector(2, 4))
    println(s5)
  }

}
