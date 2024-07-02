class Box[+T](val value: T):
  def set[U >: T](newValue: U): Box[U] = new Box(newValue)

sealed trait Animal(name: String)
case class Dog(name: String) extends Animal(name)
case class Cat(name: String) extends Animal(name)

val cats = List(Cat("Whiskers"))

Dog("Nestor") +: cats


trait Show[-T]:
  def show(value: T): String

given Show[Animal] = new Show[Animal]:
  def show(value: Animal): String = value match
    case Dog(name) => s"Dog: $name"
    case Cat(name) => s"Cat: $name"

// given Show[Dog] = new Show[Dog]:
//   def show(value: Dog): String = s"Dog: ${value.name} is barking"

given Show[Cat] = new Show[Cat]:
  def show(value: Cat): String = s"Cat: ${value.name} is meowing"

def show[T](value: T)(using s: Show[T]): String = s.show(value)

show(Dog("Whiskers"))
show(Cat("Whiskers"))
