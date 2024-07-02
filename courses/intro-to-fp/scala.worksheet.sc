sealed trait Json:
  def stringify: String = this match
    case JNull => "null"
    case JBool(value) => value.toString
    case JNum(value) => value.toString
    case JStr(value) => s""""$value""""
    case JArr(values) => values.map(_.stringify).mkString("[", ",", "]")
    case JObj(fields) => fields.map { case (k, v) => s""""$k":${v.stringify}""" }.mkString("{", ",", "}")

case object JNull extends Json
case class JBool(value: Boolean) extends Json
case class JNum(value: Double) extends Json
case class JStr(value: String) extends Json
case class JArr(values: List[Json]) extends Json
case class JObj(fields: Map[String, Json]) extends Json

JObj(Map("name" -> JStr("Alice"), "age" -> JNum(30))).stringify

object IsPositive {
  def unapply(json: Json): Option[Double] = json match {
    case JNum(value) if value > 0 => Some(value)
    case _ => None
  }
}

def testIsPositive(json: Json): Unit = json match {
  case IsPositive(n) => println(s"Positive number: $n")
  case _ => println("Not a positive number")
}

testIsPositive(JStr("hello"))
testIsPositive(JNum(42))
testIsPositive(JNum(-5))

// 4. Create an extractor `NestedArrays` on a `JArr` that matches if at least one element of the array is an array

object NestedArr {
  def unapply(json: Json): Option[Json] = json match {
    case JArr(fields) => fields.collectFirst { case jarr: JArr => jarr }
    case _ => None
  }
}

val json = JArr(List(JArr(List(JNum(1), JNum(2))), JStr("hello")))
json match {
  case NestedArr(_: JArr) => println(s"Is double nested !")
  case _ => println("No match")
}

def foldLeft[A, B](list: List[A], acc: B)(f: (B, A) => B): B = list match {
  case Nil => acc
  case head :: tail => foldLeft(tail, f(acc, head))(f)
}
def sequence(xs: List[Option[Int]]): Option[List[Int]] =
  def loop(xs: List[Option[Int]], acc: List[Int]): Option[List[Int]] = xs match {
    case Nil => Some(acc.reverse)
    case None :: _ => None
    case Some(x) :: tail => loop(tail, x :: acc)
  }
  loop(xs, Nil)

sequence(List(Some(1), Some(2), Some(3))) // Some(List(1, 2, 3))
sequence(List(Some(1), None, Some(3))) // None







