package calculator

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetLengthTest extends FunSuite {

  test("tweetRemainingCharsCount") {
    val rem = TweetLength.tweetRemainingCharsCount(Signal("ahmed"))
    println(rem())
  }

  test("option") {
    val option: Option[Int] = None
    val result = option.fold {
      Some(0)
    }{i =>
      Some(i * 10)
    }
    println(s"result: $result")
  }
}
