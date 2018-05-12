import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WaterPouringTest extends FunSuite {

  test("moves") {
    val waterPouring = new WaterPouring(Vector(4, 7))

    // Pours
    val s1 = waterPouring.Pour(1, 0).change(Vector(0, 7))
    assert(s1 === Vector(4,3))

    val s2 = waterPouring.Pour(1, 0).change(Vector(1, 5))
    assert(s2 == Vector(4,2))

    val s3 = waterPouring.Pour(1, 0).change(Vector(4, 5))
    assert(s3 == Vector(4,5))

    val s4 = waterPouring.Pour(0, 1).change(Vector(2, 7))
    assert(s4 == Vector(2, 7))

    val s5 = waterPouring.Pour(0, 1).change(Vector(2, 4))
    assert(s5 == Vector(0, 6))

    // Fills
    val s6 = waterPouring.Fill(0).change(Vector(0, 7))
    assert(s6 == Vector(4, 7))

    // Empty
    val s7 = waterPouring.Empty(0).change(Vector(4, 7))
    assert(s7 == Vector(0, 7))

    val s8 = waterPouring.Empty(1).change(Vector(4, 7))
    assert(s8 == Vector(4, 0))
  }
}
