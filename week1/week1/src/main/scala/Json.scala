

abstract class Json

case class JSeq (elems: List[Json])           extends Json
case class JObj (bindings: Map[String, Json]) extends Json
case class JNum (num: Double)                 extends Json
case class JStr (str: String)                 extends Json
case class JBool (b: Boolean)                 extends Json
case object JNull                             extends Json

class Animal

object Json {

  def main(args: Array[String]): Unit = {

    val json = JObj(
      Map(
        "firstName" -> JStr("John"),
        "lastName" -> JStr("Smith"),
        "address" -> JObj(
          Map("streeAddress" -> JStr("21 2nd Street"),
            "state" -> JStr("NY"),
            "postalCode" -> JNum(10021)
          )
        ),
        "phoneNumbers" -> JSeq(List(
          JObj(Map("type" -> JStr("home"), "number" -> JStr("212 555-1234"))),
          JObj(Map("type" -> JStr("fax"), "number" -> JStr("646 555-4567")))
        ))
      )
    )

    val str = show(JSeq(List(
      JObj(Map("type" -> JStr("home"), "number" -> JStr("212 555-1234"))),
      JObj(Map("type" -> JStr("fax"), "number" -> JStr("646 555-4567")))
    )))

    println(show(json))
  }

  def show(json: Json): String = json match {
    case JSeq(elems) =>
      "[" + elems.map(show).mkString(", ") + "]"
    case JObj(bindings) =>
      "{" +
        bindings.map { case (key, value) => '\"' + key + "\": " + show(value) }.mkString(", ")  +
        "}"
    case JNum(num) => num.toString
    case JStr(str) => '\"' + str + '\"'
    case JBool(b) => b.toString
    case JNull => "null"
  }


}