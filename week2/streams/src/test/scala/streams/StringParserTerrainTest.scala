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

  val parser2: StringParserTerrain = new StringParserTerrain {
    /**
      * A ASCII representation of the terrain. This field should remain
      * abstract here.
      */
    override val level: String =
      """oooo
        |oooo
        |ooSo
        |oooT""".stripMargin
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

  test("neighbours small terrain") {
    import parser._
    val expected = List((Block(Pos(0,-2),Pos(0,-1)),Left), (Block(Pos(0,1),Pos(0,2)),Right), (Block(Pos(-2,0),Pos(-1,0)),Up), (Block(Pos(1,0),Pos(2,0)),Down))
    assert(parser.startBlock.neighbors === expected)
  }

  test("legalNeighbours small terrain") {
    import parser._
    val expected = List((Block(Pos(1,0),Pos(2,0)),Down))
    assert(parser.startBlock.legalNeighbors === expected)
  }

  test("neighbours medium terrain") {
    import parser2._
    val expected = List((Block(Pos(2,0),Pos(2,1)),Left), (Block(Pos(2,3),Pos(2,4)),Right), (Block(Pos(0,2),Pos(1,2)),Up), (Block(Pos(3,2),Pos(4,2)),Down))
    assert(parser2.startBlock.neighbors === expected)
  }

  test("legalNeighbours medium terrain") {
    import parser2._
    val expected = List((Block(Pos(2,0),Pos(2,1)),Left), (Block(Pos(0,2),Pos(1,2)),Up))
    assert(parser2.startBlock.legalNeighbors === expected)
  }

}
