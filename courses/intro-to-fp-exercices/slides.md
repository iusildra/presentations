---
theme: ../../theme
# background: https://cover.sli.dev
title: Intro to FP - Exercices
layout: intro
info: |
  Companion exercices for the course
# apply any unocss classes to the current slide
# class: text-center
# https://sli.dev/custom/highlighters.html
highlighter: shiki
# https://sli.dev/guide/drawing
drawings:
  persist: false
# slide transition: https://sli.dev/guide/animations#slide-transitions
transition: slide-left
# enable MDC Syntax: https://sli.dev/guide/syntax#mdc-syntax
mdc: true
lineNumbers: true
hideInToc: true
addons:
  - "@twitwi/slidev-addon-ultracharger"
date: "2024-2025"
venue: "Polytech Montpellier"
author: Lucas Nouguier
addonsConfig: {
  ultracharger: {
    metaFooter: 'date venue'
  }
}
---

# Introduction to Functional Programming --- Exercices

---
hideInToc: true
---

# Table of content

<Toc maxDepth="1"/>

---
layout: section
---

# Functional paradigm

---

## Functions

1. 🟢 ~~Define a function `f` taking two integers and returns their sum~~
2. 🟢 Define a function `t` taking an int `x` and a function `g` and returns `g` applied to `x`
3. 🟢 Define a function `z` taking 2 functions `g` and `h` and returns $g\circ h$
4. 🟡 Define a function `actOnEvens` taking a list of int `ints` and returning a function that acts on even numbers of `ints`
5. 🔴 Define a pipeline function taking a list of functions `fs` and an initial value `x`, then applies each function of `fs` to `x` in sequence
6. 🔴 Write a pseudo-program (just signatures) to validate an order and its payment (once error handling has been seen)
7. 💀 Define a functor and implement it for `List`. Then do the same for `Option` (once the CAT has been seen)

---

### Solutions

````md magic-move

```scala
// 1.
def f(x: Int, y: Int): Int = x + y

// 2.
def t(x: Int, g: Int => Int): Int = g(x)

// 3.
def z[T, U, V](g: U => V, h: T => U): T => V = t => g(h(t))

// 4.
def actOnEvens(ints: List[Int]): (Int => Int) => List[Int] =
  val evens = ints.filter(_ % 2 == 0)

  (f: Int => Int) => evens.map(f)

// 5.
def pipeline[T](fs: (T => T)*)(x: T): T =
  fs.foldLeft(x)((acc, f) => f(acc))
```

```scala
// 6. Pseudo-code
def validateOrder(cart: Cart): Either[CartStatus, Cart] = ???
def checkout(cart: Cart): Either[CheckoutError, Cart]   = ???
```

```scala
// 6. Pseudo-code
def validateOrder(cart: Cart): Either[CartStatus, Cart] = ???
def checkout(cart: Cart): Either[CheckoutError, Cart]   = ???

val orderProcessing =
  for {
    cart <- validateOrder(cart)
    payment <- checkout(cart)
  } yield payment
```

```scala
// 6. Pseudo-code
def validateOrder(cart: Cart): Either[CartStatus, Cart] = ???
def checkout(cart: Cart): Either[CheckoutError, Cart]   = ???

val orderProcessing =
  for {
    cart    <- validateOrder(cart)
    payment <- checkout(cart)
  } yield payment

orderProcessing match {
  case Right(_)                           =>
    /* send confirmation */
  case Left(CartStatus.Invalid)           =>
    /* display error */
  case Left(CartStatus.Empty)             =>
    /* naughty user */
  case Left(CheckoutError.PaymentFailed)  =>
    /* payment failed, please retry */
}
```

```scala {-7|-11|-15|all}
/* ----------------------------------------------------------------------- */
/*            7. Define a functor and implement it for `List`              */
/* ----------------------------------------------------------------------- */

// Contrat definition
trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]

// Implementation
val listFunctor: Functor[List] = new Functor[List]:
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

// Usage
def generic10x[F[_]](f: F[Int])(fa: Functor[F]): F[Int] =
  fa.map(f)(_ * 10)

generic10x(List(1, 2, 3))(listFunctor) // : List[Int]    = List(10, 20, 30)
```

```scala
/* ----------------------------------------------------------------------- */
/*            7. Define a functor and implement it for `List`              */
/* ----------------------------------------------------------------------- */

// Contrat definition
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

generic10x(List(1, 2, 3))(listFunctor)  // : List[Int]   = List(10, 20, 30)
generic10x(Some(5))(optionFunctor)      // : Option[Int] = Some(50)
```

````

---

## Pattern matching

1. 🟢 Given a string `s`, pattern match on several values of `s` and return a different value for each case
2. 🟢 Given an integer `x`, return a string saying wether `x` is even or odd
3. 🟢 Given the following data structure, display the node kind (or class name)
   ```scala
    sealed trait Tree
    case class Leaf(value: Int) extends Tree
    case class Branch(left: Tree, right: Tree) extends Tree
   ```
4. 🟡 Given the following hierarchy, say wether \<name\> is a young, adult or old cat or dog
   ```scala
    sealed trait Animal
    case class Dog(id: Int, name: String, age: Int) extends Animal
    case class Cat(id: Int, name: String, age: Int) extends Animal
   ```
5. 🔴 Given the `Animal` trait defined above, extract the id of a cat older than 13 years old, one with "raw" tests, 2 with extractors

---

### Solutions

````md magic-move

```scala
// 1.
"hello" match
  case "world"  => "goodbye"
  case "hello"  => "hi"
  case _        => "..."
// : String = "hi"

// 2.
42 match
  case x if x % 2 == 0  => "even"
  case _                => "odd"
// : String = "even"

//3.
val tree: Tree = ???
tree match
  case _: Leaf   => "Leaf"
  case _: Branch => "Branch"

// 4.
Cat(1, "Scala", 13) match
  case Cat(_, _, age) if age < 2  => "young cat"
  case Cat(_, _, age) if age < 8  => "adult cat"
  case Cat(_, _, _)               => "old cat"
  // same for Dog
```

```scala {1-9|1-4,11-16|1-4,18-23}
/* -------------------------------------------------------------------------- */
/*                           5. Pattern extractors                            */
/* -------------------------------------------------------------------------- */
val cat: Animal = Cat(1, "Scala", 13)

// Simple and direct check, less readable
cat match
  case Cat(id, _, age) if age > 13  => id
  case _                            => ???

// Partial custom extractor, carries more semantic, a bit more readable
object Old:
  def unapply(age: Int): Some[Int] = if age > 13 then Some(age) else None
cat match
  case Cat(id, _, Old(age)) => id
  case _                    => ???

// Fully custom extractor, carries even more semantic
object OldCat:
  def unapply(cat: Cat): Option[Int] = if cat.age > 13 then Some(cat.id) else None
cat match
  case OldCat(id) => id
  case _          => ???
```

````

---

## Tail recursion

1. 🟢 Define the `sum` method on a list
2. 🟢 Define the `max` method on a list
3. 🟡 Define the `filter` method on a list
4. 🔴 Define the `flatMap` method on a list

---

### Solutions

````md magic-move

```scala {-6|8-14|16-}
// 1.
def sum(xs: List[Int]): Int =
  def loop(xs: List[Int], acc: Int): Int = xs match
    case Nil     => acc
    case x :: xs => loop(xs, acc + x)
  loop(xs, 0)

// 2. !! Considering the list not to be empty !!
def max(xs: List[Int]): Int =
  def loop(xs: List[Int], acc: Int): Int = xs match
    case Nil     => acc
    case x :: xs =>
      loop(xs, if x > acc then x else acc)
  loop(xs.tail, xs.head)

// 3.
def filter(xs: List[Int])(p: Int => Boolean): List[Int] =
  def loop(xs: List[Int], acc: List[Int]): List[Int] = xs match
    case Nil     => acc
    case x :: xs =>
      if p(x) then loop(xs, x :: acc)
      else loop(xs, acc)
  loop(xs, Nil).reverse // reverse to keep the order
```

```scala
// 4.
def flatMap[T, U](f: T => List[U])(xs: List[T]) =
  def loop(rest: List[T], acc: List[U]): List[U] =
    rest match
      case Nil => acc
      case head :: tail => loop(tail, acc ++ f(head))
  loop(xs, Nil)
```

````

---

## Lazy evaluation

1. 🟢 Define a lazy value `x` that prints a message to the console then evaluates to `42` when called. Call it twice. What do you notice ?
2. 🟢 Now, add a new lazy value `y` that prints a message to the console then evaluates to `x + 1` when called. Call it twice. What do you notice ?
3. 🟡 Define a function `f` taking an int `x` and a predicate `p` on `x`, with a fallback value if `!p(x)`. Don't evaluate the fallback if `p(x)` is true 😉
4. 🔴 Define a function `g` taking a list of int `ints: List[Int]` and a predicate `p: Int => Boolean` that replace the element that do not match with a *single* fallback value `System.currentTimeMillis()`

---

### Solutions

````md magic-move

```scala {-4|6-9|11-14|16-}
// 1.
lazy val x: Int = { println("x"); 42 }
x // "x" 42
x // 42

// 2.
lazy val y: Int = { println("y"); x + 1 }
y // "y" "x" 43
y // "x" 43

// 3.
def f(x: Int, p: Int => Boolean, fallback: => Int): Int =
  if p(x) then x else fallback

// 4.
def g(ints: List[Int], p: Int => Boolean, fallback => Int): List[Int] =
  ints.map(x => if p(x) then x else fallback)
```

```scala {16-}
// 1.
lazy val x: Int = { println("x"); 42 }
x // "x" 42
x // 42

// 2.
lazy val y: Int = { println("y"); x + 1 }
y // "y" "x" 43
y // "x" 43

// 3.
def f(x: Int, p: Int => Boolean, fallback: => Int): Int =
  if p(x) then x else fallback

// 4.
def g(ints: List[Int], p: Int => Boolean, fallback => Int): List[Int] =
  ints.map(x => if p(x) then x else fallback)
  // Do we really need to evaluate the fallback for each element ?
```

```scala {16-}
// 1.
lazy val x: Int = { println("x"); 42 }
x // "x" 42
x // 42

// 2.
lazy val y: Int = { println("y"); x + 1 }
y // "y" "x" 43
y // "x" 43

// 3.
def f(x: Int, p: Int => Boolean, fallback: => Int): Int =
  if p(x) then x else fallback

// 4.
def g(ints: List[Int], p: Int => Boolean, fallback => Int): List[Int] =
  lazy val fallbackCached = fallback
  ints.map(x => if p(x) then x else fallbackCached)
```

````

---

# Scala

1. 🟢 Define a class `Person` with a name and an age. Compare 2 instance of `Person` with the same attributes value. Try to pattern match on `age < 18`
2. 🟢 Redefine your `Person` to be a `case class`
3. 🟢 Define a trait `Shape` with three cases `Circle`, `Rectangle` and `Square` and pattern match on `Circle` and `Square` to print the shape kind. Now seal it.
4. 🟡 List is a covariant type. Implement the `prepend` method [in this gist](https://gist.github.com/iusildra/d991b1439f5f3205c7980bd4cdae46ae) (don't forget to add the `+` symbol 😉). Then define a list of cat and try to prepend a dog.
5. 🔴 Find out what is wrong with the 2nd part of the code in the above gist

---

# Functional data structures

1. 🟢 Compute the sum of a list of int using one of the collection function
2. 🟡 Given a list of Person, keep only the student and make it a list of Student
    ```scala
    trait Person
    case class Student(name: String)
    case class Teacher(name: String)
    ```

---

# Error handling

1. 🟢 Given a value typed as `Int | Null`, wrap it in an `Option` and increment the value
2. 🟢 Given an `Option[Int]`, double the value if it's even, otherwise return an empty value `None`
3. 🟡 Given 2 options of `Int`, combine their value. Then fallback to `-1` if the value is empty
4. 🔴 Given the error hierarchy below, and 2 `Either[MyError, Int]`, combine the 2 values. `Recoverable` values fallback to `42`, otherwise, stick to the `Fatal`
    ```scala
    sealed trait MyError
    case class Fatal(message: String)
    case class Recoverable(message: String)

    val e1: Either[MyError, Int] = Right(42)
    val e2: Either[MyError, Int] = Left(Fatal("boom"))
    ```

---

## Solutions

```scala
// 1.
Option(42).map(_ + 1)   // Some(43)

// 2.
val (a, b) = (Option(42), Option.empty[Int])
a.flatMap(x => b.map(_ + x)).getOrElse(-1) // -1

// 3.
sealed trait MyError
case class Fatal(message: String)       extends MyError
case class Recoverable(message: String) extends MyError
val e1: Either[MyError, Int] = Left(Recoverable("not boom"))
val e2: Either[MyError, Int] = Right(42)

for
  a <- e1.left.flatMap:
    case Recoverable(message) => Right(42)
    case e                    => Left(e)
  b <- e2.left.flatMap:
    case Recoverable(message) => Right(42)
    case e                    => Left(e)
yield a + b
```

---

# Categories
