---
layout: section
---

# Categories Theory

---

## What is it?

Category theory is at the root of functional programming and how we reason about programs. We have:

- Categories: our types and their values
- Morphisms: our transformations functions

```scala
val anOption: Option[Int] = Some(2)
anOption.map(x => s"$x")
// res: Option[String] = Some("2")
```

---

## How it differs from OOP

| Inheritance{v-click="[0, 1]"} | Composition {v-click="[1, 2]"} | Category theory / type classes {v-click="2"} |
| :---------------------------: | :----------------------------: | :------------------------------------------: |

````md magic-move

```scala
trait Encoder { def encode: String }
trait Combiner[A] { def combine(x: A): A }

trait Animal extends Encoder with Combiner[Animal]

case class Cat() extends Animal:
  def encode
  def combine(x: Animal)

Cat().encode // "A cat"
```

```scala
trait Encoder { def encode: String }
trait Combiner[A] { def combine(x: A): A }

trait Animal(encoder: Encoder, combiner: Combiner[Animal]):
  def encode = encoder.encode
  def combine(x: Animal) = combiner.combine(a)

case class Cat(encoder: Encoder, combiner: Combiner[Animal]) extends Animal(encoder, combiner)

Cat(anEncoder, aCombiner).encode // "A cat"
```

```scala
trait Encoder[-A] { def encode(x: A): String }
extension[A](a: A)
  def encode(using encoder: Encoder[A]): String = encoder.encode(a) // <- morphism
```

```scala {5,6|8,9}
trait Encoder[-A] { def encode(x: A): String }
extension[A](a: A)
  def encode(using encoder: Encoder[A]): String = encoder.encode(a)

trait Animal
case class Cat() extends Animal

given Encoder[Cat] with { def encode(x: Cat) = "A cat" }
Cat().encode // "A cat"
```

````

<v-click at=5>

In the CAT, we clearly separate the core logic of a type from other behaviors that can be added to it. We add behaviors by composition, without the need of a `Utils` class.

Every behaviors are wired to the type, so we still have auto-completion on added behaviors and don't need to know the full API of the `Utils` class.

In Scala, CAT is implemented through type classes and extension methods

</v-click>

---

## Type class exercice

```scala
case class Person(name: String, age: Int, address: String)
```

1. Define a type-class `Encoder` with a method `encode` that takes a `T` and returns a `String`.
2. Implement it for `Person` in JSON format
3. Implement it for `List[T]` and `Option[T]`
4. Implement it for `List[Option[T]]` and encode a `List[Option[Person]]`

Syntax reminders:

```scala
// Define a contextual value 
given Type with { /* methods */ }
// Define a generic contextual value 
given [A]: Type[A] with { /* methods */ }
// Define a generic contextual value with a dependency
given [A](using OtherType[A]): Type[A] with { /* methods */ }

// Declare a dependency on a contextual value
def foo(using a: Type): String
// short syntax for generic types
def foo[A: Type](a: A): String

```

---

## Type classes exercice --- solution

````md magic-move

```scala
// 1. TC definition
trait Encoder[-A]: // Why the minus sign ?
  def encode(x: A): String

extension [T](t: T)(using encoder: Encoder[T])
  def encode: String = encoder.encode(t)
```

```scala {6-8}
// 1. TC definition
trait Encoder[-A] { def encode(x: A): String }
extension [T](t: T)(using encoder: Encoder[T])
  def encode: String = encoder.encode(t)

// 2. Implement it for Person
given jsonEncoder: Encoder[Person] with
  def encode(x: Person) = s"""{"name": "${x.name}", "age": ${x.age}, "address": "${x.address}"}"""
```

```scala {3-4,10-12|3-4,14-16}
// 1. TC definition
trait Encoder[-A] { def encode(x: A): String }
extension [T](t: T)(using encoder: Encoder[T])
  def encode: String = encoder.encode(t)

// 2. Implement it for Person
given jsonEncoder: Encoder[Person] with
  def encode(x: Person) = s"""{"name": "${x.name}", "age": ${x.age}, "address": "${x.address}"}"""

// 3. Implement it for List[T] 
given [T: Encoder]: Encoder[List[T]] with
  def encode(x: List[T]) = x.map(_.encode).mkString("[", ", ", "]")

// 3. Implement it for Option[T] 
given [T: Encoder]: Encoder[Option[T]] with
  def encode(x: Option[T]) = x.map(_.encode).getOrElse("null")
```

```scala {18-24}
// 1. TC definition
trait Encoder[-A] { def encode(x: A): String }
extension [T](t: T)(using encoder: Encoder[T])
  def encode: String = encoder.encode(t)

// 2. Implement it for Person
given jsonEncoder: Encoder[Person] with
  def encode(x: Person) = s"""{"name": "${x.name}", "age": ${x.age}, "address": "${x.address}"}"""

// 3. Implement it for List[T] 
given [T: Encoder]: Encoder[List[T]] with
  def encode(x: List[T]) = x.map(_.encode).mkString("[", ", ", "]")

// 3. Implement it for Option[T] 
given [T: Encoder]: Encoder[Option[T]] with
  def encode(x: Option[T]) = x.map(_.encode).getOrElse("null")

List(Some(lulu), None, Some(zozo)).encode
// res: String = 
// "[
//  {"name": "lulu", ...},
//  null,
//  {"name": "zozo", ...}
// ]"
```

````

---

## Going Further

<v-clicks>

You've now seen `map` on `List`, `Option`, `Try`, `Either`... it's the same pattern!

Type classes let us write code that works with **any** mappable thing:
- One `encode` implementation for `Person`, `List[Person]`, `Option[Person]`, etc.
- Libraries like **Cats** and **ZIO** provide advanced type classes (`Functor`, `Monad`, `Applicative`)
- These abstractions enable powerful composition patterns

</v-clicks>

<v-click>

**For this course**: Focus on the practical patterns (`map`, `flatMap`, `for-yield`). The formal type class hierarchy is useful to explore later!

</v-click>
