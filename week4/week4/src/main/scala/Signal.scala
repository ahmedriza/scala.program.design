import scala.util.DynamicVariable

class Signal[T](name: String)(expr: => T) {

  import Signal._

  private var myExpr: () => T = _
  private var myValue: T = _

  private var observers: Set[Signal[_]] = Set()

  update(expr)

  protected def update(expr: => T): Unit = {
    myExpr = () => expr
    computeValue()
  }

  protected def computeValue(): Unit = {
    println(s"this: $this, computeValue, myValue $myValue")
    val newValue = caller.withValue(this)(myExpr())
    if (myValue != newValue) {
      myValue = newValue
      val obs = observers
      println(s"this: $this, obs = $obs, newValue = $newValue")
      observers = Set()
      obs.foreach(_.computeValue())
    }
  }

  def apply(): T = {
    observers += caller.value
    println(s"this: $this, caller: $caller, observers: $observers")
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }

  override def toString: String = name
}

object Signal {

  private val caller = new DynamicVariable[Signal[_]](NoSignal)

  def apply[T](name: String)(expr: => T) = new Signal(name)(expr)
}
