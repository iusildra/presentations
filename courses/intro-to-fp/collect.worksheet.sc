List(1, 2, 3, 4, 5).collect{
  case x if x % 2 != 0 => x
}

trait Animal(name: String)
case class Dog(name: String) extends Animal(name)
case class Cat(name: String) extends Animal(name)


val animals = List(Dog("Rex"), Cat("Whiskers"))
animals.collect{
  case dog: Dog => dog
}


def foo[A[_]](fa: A[Int]): A[Int] = fa