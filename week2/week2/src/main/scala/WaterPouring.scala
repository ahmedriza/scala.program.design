

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
    override def change(state: State): State = state.updated(glass, capacity(glass))
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

  val moves: List[Move] =
    (
      (for (g <- glasses) yield Empty(g)) ++
        (for (g <- glasses) yield Fill(g)) ++
        (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))
      ).toList

  // Paths
  // Path is a history of moves
  // Last move comes first in the given history, since we prepend new moves in the extend method
  class Path(history: List[Move]) {

    def endState: State = // trackState(history) (this is really a foldRight, so we can replace with foldRight)
      history.foldRight(initialState) ( (move, state) => move.change(state))

    def extend(move: Move): Path = new Path(move :: history)

    private def trackState(xs: List[Move]): State = xs match {
      case Nil => initialState
      case move :: ys =>
        val s = trackState(ys)
        val r = move.change(s)
        println(s"$s -> $r, (move: $move)")
        r
    }

    override def toString: String = (history.reverse mkString " ") + " --> " + endState
  }

  // initial starting path
  val initialPath: Path = new Path(Nil)

  /**
    * Evolve paths from a given set of initial paths
    * @param paths initial path set to start from
    * @param explored already explored paths
    * @return
    */
  def from(paths: Set[Path], explored: Set[State]): Stream[Set[Path]] =
    if (paths.isEmpty) Stream.empty
    else {
      // Generate all possible paths
      val more: Set[Path] = for {
        path <- paths
        next <- moves.map(m => path.extend(m))
        if !(explored contains next.endState)
      } yield next
      paths #:: from(more, explored ++ more.map(p => p.endState))
    }

  // All possible paths
  val pathSets: Stream[Set[Path]] = from(Set(initialPath), Set(initialState))

  /**
    * Find the path that contains the given target volume
    * @param target target volume
    * @return stream
    */
  def solution(target: Int): Stream[Path] = {
    val tmp: Stream[Path] = for {
      pathSet <- pathSets
      path <- pathSet
      if path.endState.contains(target)
    } yield path

    tmp
  }
}

object WaterPouring {

  // States
  type State = Vector[Int]

  def evolve(states: Set[State], waterPouring: WaterPouring): Set[State] = {
    val set = for {
      s <- states
      change <- waterPouring.moves.map(m => {
        val r = m.change(s)
        if (r.contains(6)) {
          println(s"state: $s, move: $m -> $r")
        }
        r
      })
    } yield change
    set
  }

  def main(args: Array[String]): Unit = {
    val waterPouring = new WaterPouring(Vector(4, 9))

    println("moves: ")
    waterPouring.moves.foreach(println)
    println("--------------------------------------------")

    val s1: Set[State] = evolve(Set(waterPouring.initialState), waterPouring)
    val s2: Set[State] = evolve(s1, waterPouring)
    val s3: Set[State] = evolve(s2, waterPouring)
    val s4: Set[State] = evolve(s3, waterPouring)
    val s5: Set[State] = evolve(s4, waterPouring)
    val s6: Set[State] = evolve(s5, waterPouring)
    val s7: Set[State] = evolve(s6, waterPouring)
    val s8: Set[State] = evolve(s7, waterPouring)

    println()

    val result = waterPouring.solution(6)
    println(result)

  }

}
