import scala.collection.immutable

/**
 The good:
   - Decouples views from state
   - Allows having a varying number of views of a given state.
   - Simple to setup

 The bad:
   - Forces imperative style, since handlers are Unit typed
   - Many moving parts that need to be coordinated.
   - Concurrency makes things more complicated
   - Views are still tightly bound to one state; view update happens immediately

  */
object ObserverPattern {

  trait Subscriber {
    def handler(publisher: Publisher)
  }

  trait Publisher {
    private var subscribers: Set[Subscriber] = Set()

    def subscribe(subscriber: Subscriber): Unit = {
      subscribers += subscriber
    }

    def unsubscribe(subscriber: Subscriber): Unit = {
      subscribers -= subscriber
    }

    def publish(): Unit = {
      subscribers.foreach(_.handler(this))
    }
  }

  class BankAccount extends Publisher {
    private var balance = 0

    def currentBalance: Int = balance

    def deposit(amount: Int): Unit = {
      if (amount > 0) {
        balance += amount
        publish()
      }
    }

    def withdraw(amount: Int): Unit = {
      if (0 < amount && amount <= balance) {
        balance -= amount
        publish()
      } else {
        throw new Error("Insufficient funds")
      }
    }
  }

  // A subscriber to maintain the total balance of a list of accounts.
  class Consolidator(observed: List[BankAccount]) extends Subscriber {

    observed.foreach(_.subscribe(this))

    private var total: Int = _

    compute()

    private def compute(): Unit = {
      total = observed.map(_.currentBalance).sum
      println(s"total: $totalBalance")
    }

    override def handler(publisher: Publisher): Unit = compute()

    def totalBalance: Int = total
  }


  def main(args: Array[String]): Unit = {

    val bankAccount1 = new BankAccount
    val bankAccount2 = new BankAccount

    val consolidator = new Consolidator(List(bankAccount1, bankAccount2))

    bankAccount1.deposit(20)
    bankAccount2.deposit(50)

    println(s"totalBalance: ${consolidator.totalBalance}")
    bankAccount2.withdraw(10)
    println(s"totalBalance: ${consolidator.totalBalance}")
  }
}
