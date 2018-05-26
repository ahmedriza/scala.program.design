package quickcheck

import org.scalacheck.{Prop, Properties}
import org.scalacheck.Prop.BooleanOperators

// Example showing Properties class usage
// This class can be run since Properties comes with a main method.

object StringSpecification extends  Properties("String") {

  import org.scalacheck.Prop.forAll

  property("startsWith") = forAll { (a: String, b: String) =>
    (a + b).startsWith(a)
  }

  property("endsWith") = forAll { (a: String, b: String) =>
    (a + b).endsWith(b)
  }

  property("empty concat") = forAll { (a: String, b: String) =>
    !a.isEmpty ==> ((a + b).endsWith(b))
  }

  /*
  property("nullName") = forAll { (name: String, age: Int, ms: Int, ss: Int) =>
    name == null ==>
      Prop.throws(classOf[IllegalArgumentException]) {
        ???
        // Student(name, age, ms, ss)
      }
  }
  */
}
