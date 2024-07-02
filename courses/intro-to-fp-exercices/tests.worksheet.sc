sealed trait Animal
case class Dog(id: Int, name: String, age: Int) extends Animal
case class Cat(id: Int, name: String, age: Int) extends Animal

val cat = Cat(1, "Scala", 10)

cat match
  case Cat(_, _, Old(age)) => s"Old cat: $cat"
  case _                   => "Other"

object Old:
  def unapply(int: Int): Option[Int] =
    if int > 10 then Some(int) else None

object OldCat:
  def unapply(cat: Cat): Option[Cat] =
    if cat.age > 10 then Some(cat) else None

def pipeline[T](fs: (T => T)*)(x: T): T =
  fs.foldLeft(x)((acc, f) => f(acc))

pipeline(
  (x: Int) => x + 1,
  (x: Int) => x * 2
)(1)

/* -------------------------------------------------------------------------- */
/*                                    test                                    */
/* -------------------------------------------------------------------------- */

trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]

// Implementation
val listFunctor: Functor[List] = new Functor[List]:
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
val optionFunctor: Functor[Option] = new Functor[Option]:
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)

// Usage
def generic10x[F[_]](f: F[Int])(fa: Functor[F]): F[Int] =
  fa.map(f)(_ * 10)

generic10x(List(1, 2, 3))(listFunctor)
generic10x(Some(1))(optionFunctor)

def actOnEvens(ints: List[Int]): (Int => Int) => List[Int] =
  val evens = ints.filter(_ % 2 == 0)

  (f: Int => Int) => evens.map(f)

trait Cart
trait CartStatus
trait CheckoutError
def validateOrder(cart: Cart): Either[CartStatus, Cart] = ???
def checkout(cart: Cart): Either[CheckoutError, Cart] = ???

def foo(cart: Cart) =
  for {
    cart <- validateOrder(cart)
    payment <- checkout(cart)
  } yield payment

def flatMap[T, U](f: T => List[U])(xs: List[T]) =
  def loop(rest: List[T], acc: List[U]): List[U] =
    rest match
      case Nil          => acc
      case head :: tail => loop(tail, acc ++ f(head))
  loop(xs, Nil)

sealed trait MyList[+T]:
  def prepend[U >: T](x: U): MyList[U]

case object Empty extends MyList[Nothing]:
  def prepend[U >: Nothing](x: U): MyList[U] = ???
case class Cons[T](head: T, tail: MyList[T]) extends MyList[T]:
  def prepend[U >: T](x: U): MyList[U] = ???

trait Encoder[-T]:
  def encode(t: T): String

extension [T](t: T)
  def encode(using encoder: Encoder[T]): String = encoder.encode(t)

trait Person:
  def name: String
  def age: Int
case class Student(name: String, age: Int, grade: Int) extends Person
val student = Student("Alice", 20, 10)

object Same:
  given Encoder[Student] with
    def encode(student: Student): String = "Student"
  val encodedStudent = student.encode

object Parent:
  given Encoder[Person] with
    def encode(person: Person): String = "Person"
  val encodedStudent = student.encode

sealed trait MyError
case class Fatal(message: String) extends MyError
case class Recoverable(message: String) extends MyError

val e2: Either[MyError, Int] = Right(42)
val e1: Either[MyError, Int] = Left(Recoverable("not boom"))

for {
  a <- e1.left.flatMap:
    case Recoverable(message) => Right(42)
    case e                    => Left(e)
  b <- e2.left.flatMap:
    case Recoverable(message) => Right(42)
    case e                    => Left(e)
} yield a + b
