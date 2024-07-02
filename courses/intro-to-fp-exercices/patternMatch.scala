sealed trait Animal
case class Dog(id: Int, name: String, age: Int) extends Animal
case class Cat(id: Int, name: String, age: Int) extends Animal

object PM:
  val cat: Animal = Cat(0, "Scala", 14)

  cat match
    case Cat(id, name, age) => "Kitty cat"
    case Dog(id, name, age) => "Doggy dog"
