package streams

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StringParserTerrainTest extends FunSuite {

  val parser: StringParserTerrain = new StringParserTerrain {
    /**
      * A ASCII representation of the terrain. This field should remain
      * abstract here.
      */
    override val level: String =
      """ST
        |oo
        |o-""".stripMargin
  }

  test("terrainFunction") {
    assert(parser.terrain(parser.Pos(0, 0)) === true)
    assert(parser.terrain(parser.Pos(0, 1)) === true)
    assert(parser.terrain(parser.Pos(1, 0)) === true)
    assert(parser.terrain(parser.Pos(1, 1)) === true)
    assert(parser.terrain(parser.Pos(2, 0)) === true)
    assert(parser.terrain(parser.Pos(2, 1)) === false)
  }

  test("findChar") {
    assert(parser.startPos === parser.Pos(0, 0))
    assert(parser.goal === parser.Pos(0, 1))
  }

  test("isStanding") {
    val b1 = parser.Pos(0,0)
    val b2 = parser.Pos(0,0)
    val block = parser.Block(b1, b2)
    assert(block.isStanding === true)
  }

  test("isLegal") {
    val block1 = parser.Block(parser.Pos(0,0), parser.Pos(0,0))
    assert(block1.isLegal === true)

    val block2 = parser.Block(parser.Pos(1,0), parser.Pos(1,1))
    assert(block2.isLegal === true)

    val block3 = parser.Block(parser.Pos(2,0), parser.Pos(2,1))
    assert(block3.isLegal === false)

    val block4 = parser.Block(parser.Pos(2,0), parser.Pos(3,0))
    assert(block4.isLegal === false)
  }

}
