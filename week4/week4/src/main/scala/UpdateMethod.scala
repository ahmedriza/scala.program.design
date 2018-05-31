
/**
  * Update method is a special method like apply in scala that the compiler transforms.
  *
  * For example, if we write the following in Scala
  *   val a = Array(10)
  *   a(0) = 20
  * is translated by the Scala compiler to
  *   a.update(0, 20)
  *
  * Generally, an indexed statement like f(E_1, ..., E_n) = E
  * is translated to
  *   f.update(E_1, ..., E_n, E)
  * This also works when n = 0:
  *   f() = E
  * is shorthand for f.update(E)
  *
  */
object UpdateMethod {

  def main(args: Array[String]): Unit = {

    class UserList() {
      private var list = Map(1 -> "ahmed", 2 -> "sami")

      def apply(id: Int) = list(id)

      def update(id: Int, name: String): Unit = {
        list = list + (id -> name)
      }

      def update(name: String, replacement: String): Unit = {
        for ((k,v) <- list) {
          if (v == name) {
            list = list + (k -> replacement)
          }
        }
      }

      def printContent(): Unit = println(list)
    }

    val u = new UserList

    u("ahmed") = "john"

    u.printContent()
  }
}
