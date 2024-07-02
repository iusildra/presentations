class Person(name: String, age: Int)

object Person {
  def newborn(name: String): Person = new Person(name, 0)
  def adult(name: String): Person = new Person(name, 18)
}

/**************************/

sealed trait Result {
  def describe = this match {
    case Success(value) => s"Success with value: $value"
    case Failure(error) => s"Failure with error: $error"
  }
}
case class Success(value: Int) extends Result
case class Failure(error: String) extends Result

Success(42).describe
Failure("Something went wrong").describe


/**************************/

sealed trait Expr {
  def eval: Int = this match {
    case Num(value) => value
    case Add(left, right) => left.eval + right.eval
    case Mul(left, right) => left.eval * right.eval
  }
}
case class Num(value: Int) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Mul(left: Expr, right: Expr) extends Expr

Mul(Num(4), Num(8)).eval
Add(Num(1), Mul(Num(2), Num(3))).eval