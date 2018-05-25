import scala.annotation.tailrec

/**
  * The Simulation Class
  * --------------------
  *
  * All we have left to do now is to implement the Simulation trait. The idea is to keep in every instance of
  * the Simulation trait an 'agenda' of actions to perform.
  *
  * The 'agenda' is a list of (simulated) 'events'. Each 'event' consists of an action and the time when it must
  * be produced. The agenda list is sorted in such a way that the actions to be performed first are in the beginning.
  *
  */

abstract class Simulation {

  type Action = () => Unit

  case class Event(time: Int, action: Action)

  private type Agenda = List[Event]

  private var agenda: Agenda = List()

  /**
    * current simulated time in the form of an integer
    */
  private var curtime = 0

  def currentTime: Int = curtime

  /**
    * Register an action to perform after a certain delay (relative to the current time, currentTime)
    * Inserts the task into the agenda list at the right position.
    * @param delay delay
    * @param block block of code to execute
    */
  def afterDelay(delay: Int)(block: => Unit): Unit = {
    val item = Event(curtime + delay, () => block)
    agenda = insert(agenda, item)
  }

  private def insert(ag: List[Event], item: Event): Agenda = ag match {
    case first :: rest
      if first.time <= item.time => first :: insert(rest, item)
    case _ => item :: ag
  }

  // The event handling loop removes successive elements from the agenda, and performs the associated actions.
  @tailrec
  private def loop(): Unit = agenda match {
    case first :: rest =>
      println(s"Running loop, current time, $curtime, agenda: $agenda")
      agenda = rest
      curtime = first.time
      // println(s"current time: $curtime")
      first.action()
      loop()

    case Nil => println("The End")
  }

  /**
    * Performs the simulation, until there are no more actions waiting.
    */
  def run(): Unit = {
    // install a first action that prints message to signal the start of the simulation.
    afterDelay(0) {
      println("*** simulation started, time = " + currentTime + " ***")
    }
    loop()
  }
}

