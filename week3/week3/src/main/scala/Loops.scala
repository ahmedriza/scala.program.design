import scala.annotation.tailrec

object Loops {

  def power(x: Int, exp: Int): Double = {
    var r = 1.0
    var i = exp
    while (i > 0) {
      r = r * x
      i = i - 1
    }
    r
  }

  def power_(x: Int, exp: Int): Double = {
    var r = 1.0
    var i = exp
    WHILE(i > 0) {
      r = r * x
      i = i - 1
    }
    r
  }

  @tailrec
  def WHILE(condition: => Boolean)(command: => Unit): Unit = {
    if (condition) {
      command
      WHILE(condition)(command)
    } else {
      ()
    }
  }

  // Write a function implementing a repeat loop that is used as follows
  // REPEAT {
  //   command
  // } (condition)
  // It should execute command one or more times, until condition is true.

  def REPEAT(command: => Unit)(condition: => Boolean): Unit = {
    command
    if (condition) {
      ()
    } else {
      REPEAT(command)(condition)
    }
  }

  // Harder
  // Is it also possible to obtain the following syntax?
  // REPEAT {
  //   command
  // } UNTIL (condition)
  //

  def main(args: Array[String]): Unit = {
    println(power(2, 10))
    println(power_(2, 10))

    var i = 10
    REPEAT {
      println(i)
      i = i - 1
    } (i < 0)
  }

}
