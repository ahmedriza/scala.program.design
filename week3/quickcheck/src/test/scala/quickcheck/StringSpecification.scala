package quickcheck

import org.scalacheck.Properties

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

}
