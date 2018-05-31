
class Var[T](name: String)(expr: => T) extends Signal[T](name)(expr) {

  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](name: String)(expr: => T) = new Var(name)(expr)
}
