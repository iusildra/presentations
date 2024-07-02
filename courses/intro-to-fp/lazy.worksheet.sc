import scala.collection.mutable.{Map => mMap}
import scala.util.Random

lazy val lazyX = {
  println("Evaluating x")
  42
}

lazyX
lazyX

def getOrElse[A](value: Option[A], default: => A): A =
  value match
    case Some(v) => v
    case None    => default

getOrElse(Some("a"), 10) match
  case _: Int => "an int"
  case _: String => "a string"
getOrElse(None, 42)

def memoize[A, B](expr: A => B): A => B =
  val cache = mMap.empty[A, B]
  a => cache.getOrElseUpdate(a, expr(a))

val mem = memoize((x: Int) => {
  println(s"Rabbit $x")
  x
})

mem(lazyX)

mem(lazyX)
