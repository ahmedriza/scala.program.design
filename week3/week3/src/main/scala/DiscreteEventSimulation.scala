import scala.annotation.tailrec

/**
  *
  * How to Make it Work
  * -------------------
  * The class Wire and the functions inverter, andGate, and orGate represent a small description language
  * of digital circuits.
  *
  * We now give the implementation of this class and its functions which allow us to simulate circuits. These
  * implementations are based on a simple API for discrete event simulation.
  *
  * A discrete event simulator performs 'actions', specified by the user at a given 'moment'.
  *
  * An 'action' is a function that doesn't take any parameters and which returns unit.
  *
  * The 'time' is simulated; it has nothing to do with the actual time.
  *
  * A concrete simulation happens inside an object that inherits from the trait Simulation, which has the following
  * signature:
  *
  * trait Simulation {
  *   def currentTime: Int = ???
  *   def afterDelay(delay: Int)(block: => Unit): Unit = ???
  *   def run(): Unit = ???
  * }
  *
  *
  * Class Diagram
  * -------------
  *
  * +---------------+
  * | Simulation    |
  * +---------------+
  *       |
  * +---------------+
  * |  Gates        |  Wire, AND, OR, INV
  * +---------------+
  *      |
  * +---------------+
  * |  Circuits     | HA, ADDER
  * +---------------+
  *      |
  * +---------------+
  * | My Simulation |
  * +---------------+
  *
  * Wire Class
  * ----------
  * A wire must support three basic operations
  * getSignal: Boolean (Returns current value of signal transported by the wire)
  * setSignal(sig: Boolean): Unit (Modifies the value of signal transported by the wire)
  * addAction(a: Action): Unit (Attaches a specified procedure to the 'actions' of the wire. All of the attached
  *                             actions are executed at each change of the transported signal).
  *
  */


object DiscreteEventSimulation extends Circuits with Parameters {

  import DiscreteEventSimulation._

  def halfAdderSimulation(): Unit = {
    val in1 = new Wire
    val in2 = new Wire
    val sum = new Wire
    val carry = new Wire

    halfAdder(in1, in2, sum, carry)
    probe("sum", sum)
    probe("carry", carry)

    // Lets change one of the input signals and run the simulation
    in1 setSignal true
    run()

    // Lets put an input signal on in2
    // in2 setSignal true
    // run()

    // Change signal of in1 to false
    // in1 setSignal false
    // run()
  }

  def basicSimulation(): Unit = {

    val i1 = new Wire
    val o1 = new Wire
    inverter(i1, o1)

    val i2 = new Wire
    val o2 = new Wire
    andGate(o1, i2, o2)

    // probe("i1", i1)
    // probe("o2", o2)

    // i1 setSignal false
    i2 setSignal true
    run()

    // i2 setSignal false
    // run()
  }

  def main(args: Array[String]): Unit = {
    basicSimulation()
    // halfAdderSimulation()
  }
}

