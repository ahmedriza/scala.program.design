
/**
  * Description language for digitial circuits.
  *
  * A digital circuit is composed of wires and functional components.
  * Wires transport signals that are transformed by components.
  *
  * We represent signals using booleans true and false. The base components (gates) are:
  *
  * The 'Inverted' whose output is the inverse of its input
  * The 'AND' Gate whose output is the conjunction of its inputs
  * The 'OR' Gate whose output is the disjunction of its inputs
  *
  * Other components can be constructed by combining these base components. The components have a
  * reaction time (or 'delay'), i.e. their inputs don't change immediately after a change to their inputs.
  *
  * Half Adder (HA):
  * ----------------
  *
  *           +-----+                          +-----+
  * a-+-------| OR  |--------------d-----------| AND |------ s (sum)
  *   | +-----|     |            +-----+   +---|     |
  *   | |     +-----+            | INV |   |   +-----+
  *   | |                   +----|     |---+ e
  *   | |     +-----+       |    +-----+
  *   +-|-----| AND |-------+------------------------------- c (carry)
  * b---+-----|     |
  *           +-----+
  *
  *  s = ((a | b)  &  (not (a & b))
  *
  *  c = a & b
  *
  *
  *  a  b   a|b   a&b  not(a&b)   s    c
  *  ------------------------------------------
  *  T  T    T     T      F       F    T
  *  T  F    T     F      T       T    F
  *  F  T    T     F      T       T    F
  *  F  F    F     F      T       F    F
  *
  *
  *  A Language for Digital Circuits
  *  -------------------------------
  *  We describe the elements of a digital circuit using the following Scala classes and functions. To start with,
  *  the class Wire models wires. Wires can be constructed as follows:
  *
  *  val a =  new Wire; val b = new Wire; val c = new Wire;
  *
  *  or equivalently:
  *
  *  val a, b, c = new Wire
  *
  *
  * Gates
  * -----
  *
  * Then there are the following functions. Each has a side effect that creates a gate.
  *
  * def inverter(input: Wire, output: Wire): Unit
  * def andGate(a1: Wire, a2: Wire, output: Wire): Unit
  * def orGate(o1: Wire, o2: Wire, output: Wire): Unit
  *
  * Constructing Components
  * -----------------------
  *
  * More complex components can be created from these. For example, a half-adder can be defined as follows:
  *
  * def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire): Unit = {
  *   val d = new Wire
  *   val e = new Wire
  *   orGate(a, b, d)
  *   andGate(a, b, c)
  *   inverter(c, e)
  *   andGate(d, e, s)
  * }
  *
  * This half-adder can in turn be used to define a full adder (ADDER):
  *
  *                         +-----+
  *                         |     |------------------- sum
  *   a --------------------|  HA |      +-----+
  *            +-----+  s   |     |  c2  |     |
  *   b -------|     |------|     |------| OR  |------ cout
  *            | HA  |      +-----+      |     |
  * cin -------|     |-------------------|     |
  *            |     |      c1           +-----+
  *            +-----+
  *
  * def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire): Unit = {
  *   val s = new Wire
  *   val c1 = new Wire
  *   val c2 = new Wire
  *   halfAdder(b, cin, s, c1)
  *   halfAdder(a, s, sum, c2)
  *   orGate(c1, c2, cout)
  * }
  *
  *
  * Exercise: What logical function does this program describe?
  * -----------------------------------------------------------
  *
  * def f(a: Wire, b: Wire, c: Wire): Unit = {
  *   val d, e, f g = new Wire
  *   inverter(a, d)
  *   inverter(b, e)
  *   andGate(a, e, f)
  *   andGate(b, d, g)
  *   orGate(f, g, c)
  * }                                                      +-----------------------------+----+
  *                                                        |                             |    |
  * a ------------- INV --- d -----------------------------|-----+-----+                 | OR |
  *       |                         +-----+                |     |     |                 |    |---- c
  *       --------------------------|     |                |     |     |                 |    |
  * b --------------INV --- e ------| AND |---- f ---------+     | AND |---- g ----------+----+
  *       |                         +-----+                      |     |
  *       |                                                      |     |
  *       -------------------------------------------------------+-----+
  *
  * a  b    d=not(a)  e=not(b) f=a&e  g=b&d    c=f|g
  * ----------------------------------------------------------------------
  * T  T      F          F      F      F       F
  * T  F      F          T      T      F       T
  * F  T      T          F      F      T       T
  * F  F      T          T      F      F       F
  *
  * f = a & not(b)
  * g = b & not(a)
  * c = f | g = a & not(b) | b & not(a)
  *
  * Hence this calculates a != b
  * ----------------------------
  * ----------------------------
  *
  *
  */

abstract class Circuits extends Gates {

   def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire): Unit = {
     val d, e = new Wire
     orGate(a, b, d)
     andGate(a, b, c)
     inverter(c, e)
     andGate(d, e, s)
   }

  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire): Unit = {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}
