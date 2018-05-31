
/**
  * Functional Reactive Programming
  * -------------------------------
  *
  * Reactive programming is about reacting to sequence of events that happen in time.
  *
  * Functional view: Aggregate an event sequence into a signal.
  * - a signal is a value that changes over time
  * - it is represented as a function from time to the value domain
  * - instead of propagating updates to mutable state, we define new signals in terms of existing ones.
  *
  * Example: Mouse Positions
  * ------------------------
  * Whenever the mouse moves, an event
  *   MouseMoved(toPos: Position)
  * is fired.
  *
  * FRP view:
  *   A signal,
  *     mousePosition: Signal[Position]
  *   which at any point in time represents the current mouse position
  *
  * FRP started in 1997 with the paper 'Functional Reactive Animation' by Conal Elliot and Paul Hudak and the
  * Fran library.
  *
  * Many libraries exist: Flapjax, Elm, Bacon.js, React4J.
  *
  * Event streaming data flow programming systems such as Rx are related, but the term FRP is not commonly used
  * for them.
  *
  * See also the paper: Deprecating the Oberver Pattern
  *
  * Fundamental Signal Operations
  * -----------------------------
  *
  * There are two fundamental operations over signals.
  * (1) Obtain the value of the signal at the current time. In our library this is expressed by () application.
  *   mousePosition() // the current mouse position
  *
  * (2) Define a signal in terms of other signals. In our library, this is expressed by the Signal constructor.
  *
  * def inRectangle(LL: Position, UR: Position): Signal[Boolean] =
  *   Signal {
  *     val position = mousePosition()
  *     LL <= pos && pos <= UR
  *   }
  *
  * Constant Signals
  * ----------------
  * The Signal(...) syntax can also be used to define a signal that has the same value always.
  *   val sig = Signal(3) // the signal that is always 3
  *
  * Time Varying Signals
  * --------------------
  * How we define a signal that varies with time?
  * - we can externally defined signals such as mousePosition and map over them
  * - or we can use a Var, a sub type of Signal
  *
  * Variable Signals
  * ----------------
  * Values of type Signal are immutable. But our library also defines a subclass Var of Signal for signals that can
  * be changed.
  *
  * Var provides an 'update' operation which allows the redefinition of the value of a signal from the current time on.
  *
  * val sig = Var(3)
  * sig.update(5) // From now on, sig returns 5 instead of 3.
  *
  * Due to the special nature of 'update' method in Scala, this can be abbreviated to
  * sig() = 5
  *
  * Signals and Variables
  * ---------------------
  *
  * Signals of type Var look a bit like mutable variables, where
  *   sig()
  * is de-referencing and
  *   sig() = newValue
  * is update.
  *
  * But there's a crucial difference. We can 'map' over signals, which gives us a relation between two signals
  * that is maintained automatically, at all future points in time.
  *
  * No such mechanism exists for mutable variables; we have to propagate all updates manually.  For example, if we have
  *   a = 2
  *   b = 2 * a
  *   a = a + 1
  *
  * b will not get automatically updated as a result of the change to a.
  *
  * Instead, if we had a Var signal:
  *   a() =
  *   b() = 2 * a()
  *   a() = 3
  *
  * then b() will be automatically updated to 6.
  *
  * A Simple FRP Implementation
  * ---------------------------
  *
  * Each signal maintains:
  *   - its current value
  *   - the current expression that defines the signal value
  *   - a set of observers: the other signals that depends on its value
  *
  * Then if the signal changes, all observers need to be re-evaluated.
  *
  * Dependency Maintenance
  * ----------------------
  * How do we record dependencies in observers?
  *
  * - When evaluating a signal-valued expression, need to know which signal caller gets defined or updated by the
  *   expression
  * - If we know that, then executing a sig() means adding caller to the observers of sig.
  * - When signal sig's value changes, all previously observing signals are re-evaluated and the set sig.observers
  *   is cleared
  * - Re-evaluation will re-enter a calling signal caller in sig.observers, as long as caller's value still depends
  *   on sig.
  *
  * Who is Calling?
  * ---------------
  * How do we find out on whose behalf a signal expression is evaluated?
  *
  * One simple (simplistic?) way to do this is to maintain a global data structure referring to the current caller.
  * The data structure is accessed in a stack-like fashion because one evaluation of a signal might trigger others.
  *
  * Here's a class for stackable variables:
  *
  * class StackableVariable[T](init: T) {
  *   private var values: List[T] = List(init)
  *   def value: T = values.head
  *   def withValue[R](newValue: T)(op: => R): R = {
  *     values = newValue :: values
  *     try op finally values = values.tail
  *   }
  * }
  *
  * You access it like this:
  *
  * val caller = new StackableVariable(initialSig)
  * caller.withValue(otherSig) { ... }
  *
  * We also evaluate signal expressions at the top-level when there is no other signal that's defined or updated.
  * We use the 'sentinel' object NoSignal as the caller for these expressions.
  *
  * Re-evaluating Callers
  * ---------------------
  *
  * A signal's current value can change when
  *   - somebody calls update operation on a Var, or
  *   - the value of a dependent signal changes
  *
  * Propagating requires a more refined implementation of computeValue, we originally had:
  *
  *   protected def computeValue(): Unit = {
  *     myValue = caller.withValue(this)(myExpr())
  *   }
  *
  * New version:
  *
  *   protected def computeValue(): Unit = {
  *     val newValue = caller.withValue(this)(myExpr())
  *     if (myValue != newValue) {
  *       myValue = newValue
  *       val obs = observers
  *       observers = Set()
  *       obs.foreach(_.computeValue())
  *     }
  *   }
  *
  * Handling Vars
  * -------------
  * Recall that Var is a signal that can be updated by the client program. In fact, all necessary functionality is
  * present in class Signal; we just need to expose it.
  *
  * Discussion
  * ----------
  *
  * Our implementation of FRP is quite stunning in its simplicity. But you might argue that it is too simplistic.
  * In particular, it makes use of the worst kinds of state: global state.
  *
  *   object Signal {
  *     private val caller = new StackableVariable[Signal[_] ](NoSignal)
  *     ...
  *   }
  *
  * One immediate problem is: What happens if we try to evaluate several signal expressions in parallel.
  *   - The caller signal will become 'garbled' by concurrent updates.
  *
  * Thread Local State
  * -----------------
  *
  * One way to get around the problem of concurrent access to global state is to use synchronization. But this blocks
  * threads, can be slow, and can lead to deadlocks. Another solution is to replace global state by thread-local state.
  *
  *  - Thread local state means each thread accesses a separate copy of a variable.
  *  - It is supported in Scala through class scala.util.DynamicVariable.
  *
  * The API of DynamicVariable matches the one of StackableVariable, so we can simply swap it into our Signal
  * implementation.
  *
  * Another Solution
  * ----------------
  *
  * Thread local state still comes with a number of disadvantages:
  *   - Its imperative nature often produces hidden dependencies which are hard to manage.
  *   - Its implementation on the JDK involves a global hash table lookup, which can be a performance problem.
  *   - It does not play well in situations where threads are multiplexed between several tasks (e.g. worker threads
  *     in a pool which are multiplexed over different tasks).
  *
  * A cleaner solution involves implicit parameters.
  *   - Instead of maintaining a thread-local variable, pass its current value into a signal expression as an
  *     implicit parameter.
  *   - This is purely functional. But it currently requires more boilerplate than the thread-local solution.
  *   - Future versions of Scala might solve that problem.
  *
  * Summary
  * -------
  * We have given a quick tour of functional reactive programming, with some usage examples and an implementation.
  * This is just a taster, there's much more to be discovered.
  *
  * In particular, we covered only one particular style of FRP: Discrete Signals changed by events.
  *
  * Some variants of FRP also treat continuous signals. Values in these systems are often computed by sampling instead
  * of event propagation.
  *
  */
object FRP {

  // BankAccount example with signals.

  class BankAccount {

    val balance: Var[Int] = Var("balance")(0)

    def deposit(amount: Int): Unit = {
      if (amount > 0) {
        val b = balance()
        balance() = b + amount
      }
    }

    def withdraw(amount: Int): Unit = {
      if (0 < amount && amount <= balance()) {
        val b = balance()
        balance() = b - amount
      } else {
        throw new Error("Insufficient funds")
      }
    }

    def currentBalance() = balance()
  }

  def bankAccountExample(): Unit = {
    val bankAccount = new BankAccount
    bankAccount.deposit(10)
    bankAccount.deposit(30)
    bankAccount.withdraw(10)
    bankAccount.withdraw(20)

    println(bankAccount.currentBalance())
  }

  def main(args: Array[String]): Unit = {

    val a: Var[Double] = new Var("a")(0.0)
    val x: Var[Double] = new Var("x")(0.0)

    a() = 2
    x() = 2 * a()
    a() = 4
    // println(x())


  }
}
