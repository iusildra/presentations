// import scala.util.Random
// trait Encoder[-T]:
//   def encode(x: T): String

// trait Person:
//   def name: String
// case class Chef(name: String, speciality: String) extends Person
// case class Student(name: String, speciality: String) extends Person

// val chefEncoder = new Encoder[Chef]:
//   def encode(chef: Chef): String = s"${chef.name} is a chef, specialising in ${chef.speciality}"
// val personEncoder = new Encoder[Person]:
//   def encode(person: Person): String = s"${person.name} is a person"

// def enc[T <: Person](p: T)(encoder: Encoder[T]) = encoder.encode(p)

// enc[Chef](Chef("John", "dessert"))(chefEncoder)
// enc[Chef](Chef("John", "dessert"))(personEncoder)

// val f1: Int => Int = x => x + 1
// val f1_short: Int => Int = _ + 1

// case class Element(score: Double)
// def scoreComputer(xs: List[Element]): Element => Double =
//   val best: Element = xs.head
//   (e: Element) => e.score / best.score

// val elements: List[Element]         = List(Element(8), Element(4), Element(2))
// val computeScore: Element => Double = scoreComputer(elements)
// val scores: List[Double]            = elements.map(computeScore)

// case class Product(id: Int)
// case class ProductSet(filters: Seq[String], products: Seq[Product])
// def fetchProductSetFromFilter(filter: String): Set[Product] = ???

// def fetchNProducts(n: Int, filters: Set[String]): Set[Product] =
//   filters.foldLeft(Set.empty) {
//     case (ps, _) if ps.size >= n => ps
//     case (ps, filter) =>
//       ps ++ fetchProductSetFromFilter(filter).take(n - ps.size)
//   }

// def fetchFirstNProducts(n: Int, filters: Seq[String]): Set[Product] =
//   def loop(ps: Set[Product], filters: List[String]): Set[Product] = 
//     if (ps.size >= n || filters.isEmpty) ps
//     else loop(ps ++ fetchProductSetFromFilter(filters.head).take(n - ps.size), filters.tail)
//   loop(Set.empty, filters.toList)

val add2: Int => Int =
  _ + 2

trait Encoder[-A] { def encode(x: A): String }
trait Combiner[A] { def combine(x: A): A }

extension[A](a: A)
  def encode(using encoder: Encoder[A]): String = encoder.encode(a)
  def combine(x: A)(using combiner: Combiner[A]): A = combiner.combine(a)

trait Animal

case class Cat() extends Animal

given Encoder[Cat] with { def encode(x: Cat) = "A cat" }
given Combiner[Cat] with { def combine(x: Cat) = x }

Cat().encode // "A cat"

val list = List(1, 2, 3)
list map (_ + 1)

trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]

given Functor[List] with
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
given Functor[Option] with
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)

def map[F[_]: Functor, A, B](fa: F[A])(f: A => B): F[B] = summon[Functor[F]].map(fa)(f)