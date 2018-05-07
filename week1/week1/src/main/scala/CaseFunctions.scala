object CaseFunctions {

  def main(args: Array[String]): Unit = {

    val f: PartialFunction[String, String] = {
      case "ping" => "pong"
    }

    val g: PartialFunction[List[Int], String] = {
      case Nil => "one"
      case x :: rest =>
        rest match {
          case Nil => "two"
          case _ => "???"
        }
    }

    println(f("ping"))

    println(f.isDefinedAt("ping"))
    println(f.isDefinedAt("pong"))

    println(g.isDefinedAt(List(1,2,3)))
  }
}
