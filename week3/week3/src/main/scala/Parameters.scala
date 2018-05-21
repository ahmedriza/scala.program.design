/**
  * Technology dependent parameters. It's convenient to pack delay constants into their own trait which can
  * be mixed into a simulation.
  */
trait Parameters {
  def InverterDelay = 2
  def AndGateDelay = 3
  def OrGateDelay = 5
}
