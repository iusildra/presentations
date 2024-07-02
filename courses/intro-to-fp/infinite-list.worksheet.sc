import scala.collection.mutable.ListBuffer

sealed trait InfiniteList[A]:
  def head: A
  def tail: InfiniteList[A]

  def map[B](f: A => B): InfiniteList[B]
  def flatMap[B](f: A => InfiniteList[B]): InfiniteList[B]
  def filter(p: A => Boolean): InfiniteList[A]

  def take(n: Int): InfiniteList[A]
  def toList: List[A] =
    def loop(acc: ListBuffer[A], xs: InfiniteList[A]): List[A] =
      xs match
        case Empty() => acc.toList
        case _       => loop(acc.append(xs.head), xs.tail)

    loop(ListBuffer.empty, this)

object InfiniteList:
  def from[A](start: A)(generator: A => A): InfiniteList[A] =
    new Cons(start, from(generator(start))(generator))

class Cons[A](val head: A, t: => InfiniteList[A]) extends InfiniteList[A] {
  override lazy val tail: InfiniteList[A] = t
  override def map[B](f: A => B): InfiniteList[B] =
    Cons(f(head), tail.map(f))
  def flatMap[B](f: A => InfiniteList[B]): InfiniteList[B] =
    map(f)
    ???
  def filter(p: A => Boolean): InfiniteList[A] =
    if p(head) then Cons(head, tail.filter(p))
    else tail.filter(p)
  override def take(n: Int): InfiniteList[A] =
    if n == 0 then Empty()
    else Cons(head, tail.take(n - 1))
}

case class Empty[A]() extends InfiniteList[A] {
  def head = throw new Exception("Empty.head")
  def tail = throw new Exception("Empty.tail")
  def map[B](f: A => B): InfiniteList[B] = Empty()
  def flatMap[B](f: A => InfiniteList[B]): InfiniteList[B] = Empty()
  def filter(p: A => Boolean): InfiniteList[A] = Empty()
  def take(n: Int): InfiniteList[A] = n match
    case 0 => this
    case _ => throw new Exception("Empty.take")

}

InfiniteList.from(0)(_ + 1).map(_ * 2).take(10).toList
