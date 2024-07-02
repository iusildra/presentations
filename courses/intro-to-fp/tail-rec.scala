













def sum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil => acc
    case head :: tail => loop(tail, acc + head)
  }
  loop(xs, 0)
}

def length[A](xs: List[A]): Int = {
  def loop(xs: List[A], acc: Int): Int = xs match {
    case Nil => acc
    case _ :: tail => loop(tail, acc + 1)
  }
  loop(xs, 0)
}




















def reverse[A](xs: List[A]): List[A] = {
  def loop(xs: List[A], acc: List[A]): List[A] = xs match {
    case Nil => acc
    case head :: tail => loop(tail, head :: acc)
  }
  loop(xs, Nil)
}

def flatten[A](xss: List[List[A]]): List[A] = {
  def loop(xss: List[List[A]], acc: List[A]): List[A] = xss match {
    case Nil => acc
    case head :: tail => loop(tail, head ::: acc)
  }
  loop(xss, Nil).reverse
}









def foldLeft[A, B](xs: List[A], z: B)(f: (B, A) => B): B = {
  def loop(xs: List[A], acc: B): B = xs match {
    case Nil => acc
    case head :: tail => loop(tail, f(acc, head))
  }
  loop(xs, z)
}
