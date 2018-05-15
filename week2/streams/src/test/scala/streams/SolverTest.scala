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
    *    0  1  2  3  4  5  6  7  8  9
    *   +--+--+--+
    * 0 |  |  |  |
    *   +--+--+--+--+--+--+
    * 1 |  | S|  |  |  |  |
    *   +--+--+--+--+--+--+--+--+--+
    * 2 |  |  |  |  |  |  |  |  |  |
    *   +--+--+--+--+--+--+--+--+--+--+
    * 3    |  |  |  |  |  |  |  |  |  |
    *      +--+--+--+--+--+--+--+--+--+
    * 4                |  |  | T|  |  |
    *                  +--+--+--+--+--+
    * 5                   |  |  |  |
    *                     +--+--+--+
    */

  val solver: Solver = new Solver with InfiniteTerrain {
    /**
      * The position where the block is located initially.
      *
      * This value is left abstract, it will be defined in concrete
      * instances of the game.
      */
    override val startPos = Pos(1,1)
    /**
      * The target position where the block has to go.
      * This value is left abstract.
      */
    override val goal: Pos = Pos(4,7)
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
  }
}
