import scala.collection.immutable

class Pouring(capacity: Vector[Int]) {
  // States
  type State = Vector[Int]
  val initialState: Vector[Int] = capacity map (x => 0)

  // Moves
  trait Move {
    def change(state: State): State
  }

  case class Empty(glass: Int) extends Move {
    def change(state: State): State = state updated(glass, 0)
  }

  case class Fill(glass: Int) extends Move {
    def change(state: State): State = state updated(glass, capacity(glass))
  }

  case class Pour(from: Int, to: Int) extends Move {
    def change(state: State): State = {
      val amount = state(from) min (capacity(to) - state(to))
      state updated(from, state(from) - amount) updated(to, state(to) + amount)
    }
  }

  val glasses = 0 until capacity.length

  val moves: immutable.IndexedSeq[Move with Product with Serializable] =
    (for (g <- glasses) yield Empty(g)) ++
      (for (g <- glasses) yield Fill(g)) ++
      (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))

  class Path(history: List[Move], val endState: State) {
    // def trackState(xs: List[Move]): State = xs match {
    //   case Nil => initialState
    //   case move :: xs1 => move change trackState(xs1)
    // }
    // def endState: State = { trackState(history) }
    // def endState: State = (history foldRight initialState (_ change _)
    def extend(move: Move) = new Path(move :: history, move change endState)

    override def toString: String = (history.reverse mkString " ") + "--> " + endState
  }

  val initialPath = new Path(Nil, initialState)

  def from(paths: Set[Path], explored: Set[State]): Stream[Set[Path]] = {
    if (paths.isEmpty) Stream.empty
    else {
      val more = for {
        path <- paths
        next <- moves map path.extend
        if !(explored contains next.endState)
      } yield next
      paths #:: from(more, explored ++ (more map (_.endState)))
    }

  }

  val pathSets: Stream[Set[Path]] = from(Set(initialPath), Set(initialState))

  def solutions(target: Int): Stream[Path] = {
    for {
      pathSet <- pathSets
      path <- pathSet
      if path.endState contains target
    } yield path
  }
}

object Pouring {
  def main(args: Array[String]): Unit = {
    val pouring = new Pouring(Vector(4, 9))
    val sol = pouring.solutions(6)
    println(sol)

    // (0,0) -> Fill(1)   -> (0,9)
    // (0,9) -> Pour(1,0) -> (4,5)
    // (4,5) -> Empty(0)  -> (0,5)
    // (0,5) -> Pour(1,0) -> (4,1)
    // (4,1) -> Empty(0)  -> (0,1)
    // (0,1) -> Pour(1,0) -> (1,0)
    // (1,0) -> Fill(1)   -> (1,9)
    // (1,9) -> Pour(1,0) -> (4,6)
    //
    // --> Vector(4, 6), ?)

  }

}