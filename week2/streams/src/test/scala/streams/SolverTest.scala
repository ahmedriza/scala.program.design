package streams

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SolverTest extends FunSuite {

  /**
    *   0123456789
    * 0 ooo-------
    * 1 oSoooo----
    * 2 ooooooooo-
    * 3 -ooooooooo
    * 4 -----ooToo
    * 5 ------ooo-
    */

  /**
    *  List(Right, Down, Down, Right, Right, Down, Right)
    *
    *     0  1  2  3  4  5  6  7  8  9
    *   +--+--+--+
    * 0 |  |  |  |
    *   +--+--+--+--+--+--+
    * 1 |  | S|--|--|  |  |
    *   +--+--+--+--+--+--+--+--+--+
    * 2 |  |  |--|--| x|--|--|  |  |
    *   +--+--+--+--+--+--+--+--+--+--+
    * 3    |  |  |  |  |--|--|  |  |  |
    *      +--+--+--+--+--+--+--+--+--+
    * 4                |--|--| T|  |  |
    *                  +--+--+--+--+--+
    * 5                   |  |  |  |
    *                     +--+--+--+
    */

  /**
    *   List(Right, Right, Down, Right, Right, Right, Down)
    *
    *     0  1  2  3  4  5  6  7  8  9
    *   +--+--+--+
    * 0 |  |  |  |
    *   +--+--+--+--+--+--+
    * 1 |  | S|--|--| x|  |
    *   +--+--+--+--+--+--+--+--+--+
    * 2 |  |  |  |  |- |- |- |- |  |
    *   +--+--+--+--+--+--+--+--+--+--+
    * 3    |  |  |  |- |- |- |- |  |  |
    *      +--+--+--+--+--+--+--+--+--+
    * 4                |  |  | T|  |  |
    *                  +--+--+--+--+--+
    * 5                   |  |  |  |
    *                     +--+--+--+
    */

  /**
    *
    *     0  1  2  3  4  5  6  7  8  9
    *   +--+--+--+
    * 0 |  |  |  |
    *   +--+--+--+--+--+--+
    * 1 |  | S|- |- |  |  |
    *   +--+--+--+--+--+--+--+--+--+
    * 2 |  |  |- |- |  |  |  |  |  |
    *   +--+--+--+--+--+--+--+--+--+--+
    * 3    |  | -| -|  |  |  |  |  |  |
    *      +--+--+--+--+--+--+--+--+--+
    * 4                |  |  |  |  |  |
    *                  +--+--+--+--+--+
    * 5                   |  |  |  |
    *                     +--+--+--+
    */

  val solver: Solver = new Solver with StringParserTerrain {
    /**
      * The position where the block is located initially.
      *
      * This value is left abstract, it will be defined in concrete
      * instances of the game.
      */
    override lazy val startPos = Pos(1,1)
    /**
      * The target position where the block has to go.
      * This value is left abstract.
      */
    override lazy val goal: Pos = Pos(4,7)
    /**
      * A ASCII representation of the terrain. This field should remain
      * abstract here.
      */
    override val level: String =
      """ooo-------
        |oSoooo----
        |ooooooooo-
        |-ooooooooo
        |-----ooToo
        |------ooo-""".stripMargin
  }

  val impossibleSolver: Solver = new Solver with StringParserTerrain {
    /**
      * The position where the block is located initially.
      *
      * This value is left abstract, it will be defined in concrete
      * instances of the game.
      */
    override lazy val startPos = Pos(1,1)
    /**
      * The target position where the block has to go.
      * This value is left abstract.
      */
    override lazy val goal: Pos = Pos(1,1)
    /**
      * A ASCII representation of the terrain. This field should remain
      * abstract here.
      */
    override val level: String =
      """ooo-------
        |oSoooo----
        |ooooooooT-
        |-ooooooooo
        |-----ooooo
        |------ooo-""".stripMargin
  }

  test("solve") {
    import solver._

    val b1  = Block(startPos, startPos)
    val b2 = b1.right
    val b3 = b2.down
    val b4 = b3.right
    assert(b4.isStanding === true)
    val b5 = b4.right
    val b6 = b5.down
    val b7 = b6.down
    val b8 = b7.right
    assert(b8.isStanding === true)
    println(b8)
    assert(b8.b1 == goal)
    assert(b8.b2 == goal)

    // Moves, with last first
    val history = List(Right, Down, Down, Right, Right, Down, Right)
    solver.neighborsWithHistory(b8, history).toSet foreach println
  }

  test("newNeighboursOnly with empty stream") {
    import solver._

    val result = newNeighborsOnly(Stream.empty, Set())
    assert(result === Stream.empty)
  }

  test("neighboursWithHistory") {
    import solver._
    val nbh = neighborsWithHistory(Block(Pos(1,1),Pos(1,1)), List(Left,Up)).toSet
    assert(nbh === Set(
      (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
      (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
    ))
  }

  test("newNeighboursOnly") {
    import solver._

    val result = newNeighborsOnly(
      Set(
        (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
        (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
      ).toStream,

      Set(Block(Pos(1,2),Pos(1,3)), Block(Pos(1,1),Pos(1,1)))
    )

    assert(result.toSet === Set((Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))))
  }

  test("from") {
    import solver._
    println("Paths from Start:")
    pathsFromStart.take(10).toSet foreach println
    println("------------------------")
    pathsToGoal.take(100).toSet foreach println
    println("------------------------")
    println(solution)
  }

  test("impossibl") {
    import impossibleSolver._
    // println("Impossible Solution")
    // println(solution)
  }
}
