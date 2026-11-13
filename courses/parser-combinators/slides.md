---
theme: ../../theme
title: Parser Combinators
layout: intro
info: |
  Building parsers with functional programming,
  based on Creative Scala by Noel Welsh
highlighter: shiki
drawings:
  persist: false
transition: slide-left
mdc: true
lineNumbers: true
hideInToc: true
addons:
  - "@twitwi/slidev-addon-ultracharger"
date: "2024-2025"
venue: "Polytech Montpellier"
author: Lucas Nouguier
addonsConfig:
  { ultracharger: { disable: ["tocFooter"], metaFooter: "date venue" } }
---

# Building parsers with functional programming

<div class="mt-4 flex justify-center">
  <img src="/scala-spiral.png" alt="Scala logo" class="h-40" />
</div>

Based on [Creative Scala](https://www.creativescala.org/case-study-parser/index.html) by Noel Welsh

---
hideInToc: true
---

# Table of content

<Toc maxDepth="1"/>

---
layout: section
transition: fade
---

# Introduction

---
transition: fade
---

## Parser

A **Parser** is a program that analyses a piece of text to determine its syntactic structure.

---
transition: fade
---

## ADTs

An **Algebraic Data Type** is a composite type made of other types.

```scala {1-2|4-10}
type List[A]   = Nil | Cons[A]
type Option[A] = None | Some[A]

sealed trait Tree
case class Name() extends Tree
case class Select(qual: Tree, name: Name) extends Tree
case class Method(fun: Tree, args: ArgClauses) extends Tree
case class If(p: Tree, thenp: Tree, elsep: Tree) extends Tree

case class ArgClauses(args: List[List[Tree]]) extends Tree
```

---
transition: fade
---

## Failure handling mechanisms

|           | Difficulty | Failure reason | Performances | Expressivity | Runtime failure |
| --------- | :--------: | :------------: | :----------: | :----------: | :-------------: |
| Null      |    Easy    |    Unknown     |     Best     |     Bad      |    Possible     |
| Exception |    Easy    |     Known      |     Bad      |    Better    |    Possible     |
| ADT       | Less easy  |     Known      |    Better    |     Best     |       No        |

---
transition: fade
---

## Exceptions & performances

| Benchmark      | ms/operation (avg) | Error   |
| -------------- | -----------------: | ------- |
| No exceptions  |              0.046 | ± 0.003 |
| Throw catch    |             16.268 | ± 0.239 |
| Throw no catch |             17.874 | ± 3.199 |
| Fast exception |              1.174 | ± 0.014 |

<v-click>

Roughly **350× slower** than not throwing at all.

[baeldung.com/java-exceptions-performance](https://baeldung.com/java-exceptions-performance)

</v-click>

---
transition: fade
---

## Parser Combinators

<v-switch>
<template #0>

<div class="flex justify-center">
  <img src="/lego.webp" alt="Lego" class="h-80" />
</div>

</template>
<template #1-5>

```scala {1-3|5-8|9-11|13-16} {at:2}
val tpeSep: Parser[String]  // ( : ) | (: ) | ( :)
val alphas: Parser[Char]    // [a-zA-Z]
val name: Parser[String]    = alphas.repeat.combineAll

// Parser[Param] -- x: Int
name.product(tpeSep).product(name)
  .map { (nme, _, tpeName) => Param(nme, tpeName) }

// Parser[Param] -- x: Int
(name <* tpeSep).product(name)
  .map { (nme, tpeName) => Param(nme, tpeName) }

// Parser[Method] -- def foo(x: A, y: B, z: C): XX
val methodParser =
  (methodName, methodParams, methodType)
    .mapN { case (a, b, c) => Method(a, b, c) }
```

</template>
</v-switch>

---
transition: fade
---

## Example

Given the following code, which parser can we create?

```scala
package scalala

object Main:
  val x = 1
  val y = 2
  val adder = (a: Int, b: Int) => a + b

  def main(args: Array[String]): Unit =
    println(foo(x, y))

  def foo(x: Int, y: Int) = x + y
```

<div v-click class="flex justify-center">

<img src="/parser-structure.svg" alt="Diagram of which parser uses which parsers" class="h-60" />

</div>

---
layout: section
transition: fade
---

# Scala reminders

---
transition: fade
---

## `object` keyword

In Scala, an `object` defines a singleton class and is treated as a value.

```scala {1-2|3-4} {at:1}
object Foo:
  def bar: String = "bar"
Foo.bar // "bar"
val foo = Foo // Foo$@...
```

<div v-click="2">

With a class of **same name** in the **same file**, it becomes its companion object, and its methods/fields are static members of the class.

</div>

<div v-click="3">

```scala {1-2|4-5|3} {at:4}
class Foo:
  def bar: String = "bar"
  // static def baz = "baz"
object Foo:
  def baz: String = "baz"
```

</div>

---
transition: fade
---

## Case classes

| "Feature"            |  Class  | Case Class |
| -------------------- | :-----: | :--------: |
| Attribute visibility | Private |   Public   |
| Getters              |   ❌    |     ✅     |
| `toString`           |   ❌    |     ✅     |
| `hashCode`           |   ❌    |     ✅     |
| `equals`             |   ❌    |     ✅     |
| `unapply` methods    |   ❌    |     ✅     |

<v-click>

Attribute visibility can be overridden.

</v-click>

---
transition: fade
---

## Pattern matching

A turbo-boosted `switch`:

<v-clicks>

- Match on type
- Match on value
- Exhaustive
- Recursive patterns
- Stop on first match

</v-clicks>

```scala {1|4|5|6|8|10-12|7}
case class Dev(name: String, age: Int)

person match
case Dev("Lulu", 22) => ???
case Dev("Lulu", age) if age > 60 => ???
case p @ (_: Com | _: HR) => ???
case Dev(_, Even(age)) => ???
case _ => ???

object Even:
  def unapply(i: Int): Option[Int] =
    if i % 2 == 0 then Some(i) else None
```

---
transition: fade
---

## Eager evaluation

<v-click>

Evaluation **on declaration**:

```scala
val x = 1 // : Int = 1
val sum = x + 1 // : Int = 2
```

</v-click>

---
transition: fade
---

## Lazy evaluation

<v-click>

Evaluation **on call**:

```scala
lazy val x = 1 // : Int = ???
val sum = x + 1
// 1. sum = x + 1
// 2. x = 1
// 3. sum = 2
```

</v-click>

<div v-click>

Infinite lists, caching, performance optimizations...

</div>

---
transition: fade
---

## By-value

<v-click>

Arguments evaluated **before** the body (~ eager evaluation):

```scala
def foo(x: Int) =
  for
    _ <- 0 until 3
  yield x
foo(R.nextInt(10)) // = (1, 1, 1)
```

</v-click>

---
transition: fade
---

## By-name

<v-click>

Arguments evaluated **when called** (~ lazy evaluation):

```scala
def foo(x: => Int) =
  for
    _ <- 0 until 3
  yield x
foo(R.nextInt(10)) // = (2, 8, 1)
```

</v-click>

<div v-click>

Avoids unnecessary computations.

</div>

---
transition: fade
---

## By-name example: Lazy List

```scala {1-4|13-14|6-11}
sealed abstract class LazyList[A]
  def map[B](f: A => B): LazyList[A]
  def filter(p: A => Boolean): LazyList[A]
  def forceEval(n: Int): List[A]

object LazyList:
  def generate[A](start: A)(generator: A => A): LazyList[A] =
    new LazyListCons(
      start,
      generate(generator(start))(generator)
    )

class Cons[A](h: => A, t: => LazyList[A]) extends LazyList[A]
class Empty[A] extends LazyList[A]
```

---
transition: fade
---

## By-name example: usage

```scala {1-2|4-6|8-11}
val integers = LazyList.generate(0)(_ + 1)
// integers: LazyList[Int] = LazyListCons@...

integers
  .map(_ * 2)
  .forceEval(10) // List(0, 2, ..., 16, 18)

integers
  .map(_ * 2)
  .filter(_ % 3 == 0)
  .forceEval(10) // List(0, 6, 12, ..., 42, 48, 54)
```

---
transition: fade
---

## Given/using (implicit values)

`using` defines a value to be injected by the compiler, based on the type among the `given` values in scope:

```scala {1-2|4-6}
given life: Int = 42
def foo(using i: Int): Int = i

foo // : Int = 42
// rewritten as foo(life)
foo(using life) // : Int = 42
```

<div v-click>

Shorthand for generics:

```scala
def foo1[A](...)(using t: Type[A])
// same as
def foo1[A: Type](...)
```

</div>

---
transition: fade
---

## Extension methods

In Scala we can define extension methods for any type we want. It is **not** equivalent to creating a subclass.

```scala {1-2|3|6-9|11-12}
// define extension method for any generic type
extension [A](a: A)
  def foo: String = "foo"
  ...

// define extensions methods available only
// if an instance of Something[A] is in scope
extension [A: Something](a: A)
  def bar: String = "bar"

1.foo // "foo"
"bar".foo // "foo"
```

---
layout: section
transition: fade
---

# Type class — Functional design

---
transition: fade
---

## Variance

Describes the relationship between generic types. Given a generic type $\mathcal{F}$:

<v-clicks>

- **Invariance**: $\forall A, B \quad A \neq B \Leftrightarrow \mathcal{F}[A] \neq \mathcal{F}[B]$
- **Covariance**: $\forall A, B \quad A <: B \Leftrightarrow \mathcal{F}[A] <: \mathcal{F}[B]$
- **Contravariance**: $\forall A, B \quad A <: B \Leftrightarrow \mathcal{F}[B] <: \mathcal{F}[A]$

</v-clicks>

---
transition: fade
---

## Variance constraints

It allows a more flexible design, but has some constraints for type-safety.

::div{.grid .grid-cols-2 .gap-4}

<div>

### Problem

```scala {1-2|4|5|7} {at:1}
sealed trait List[+A]:
  def ::(a: A): List[A] // prepend

val d: List[Dog] = List(Dog())
val list: List[Animal] = d

Cat() :: list // type error!
```

</div>

<div v-click="4">

### Solution

```scala {2|7} {at:5}
sealed trait List[+A]:
  def ::[B >: A](b: B): List[B]

val d: List[Dog] = List(Dog())
val list: List[Animal] = d

Cat() :: list // : List[Animal]
```

</div>

::

---
transition: fade
---

## Why type class?

<v-switch>
<template #0>

### Inheritance

```scala {1-2,4-5}
trait Encoder { def encode: String }
trait Combiner[A] { def combine(a: A): String }

abstract class Animal extends Encoder
  with Combiner[Animal]

case class Cat() extends Animal:
  override def encode: String
  override def combine(b: Animal): String

aCat.encode // "A cat"
```

</template>
<template #1>

### Composition

```scala {1,3-5}
trait Encoder[A] { def encode(a: A): String }

abstract class Animal(
  encoder: Encoder[Animal],
):
  def encode = encoder.encode(this)

aCat.encode // "A cat"
```

</template>
<template #2-6>

### Type class

```scala {1,3|5-6|8-9|11-12} {at:3}
trait Encoder[-A] { def encode(a: A): String }

abstract class Animal

def encode[A](a: A)(using encoder: Encoder[A]): String =
  encoder.encode(a)

given Encoder[Cat] with
  def encode(a: Cat) = "A cat"

extension [A](a: A)(using encoder: Encoder[A])
  def encode: String = encoder.encode(a)

aCat.encode // "A cat"
```

</template>
</v-switch>

---
transition: fade
---

## Type class definition

<v-clicks>

1. Define a trait (the behavior)
2. Define methods using an instance of the trait
3. Define `given` instance
4. (Optional) Redefine methods as extension methods

</v-clicks>

---
transition: fade
---

## Exercices

```scala
case class Person(name: String, age: Int, address: String)
```

<v-clicks>

1. Define a type-class `JsonEncoder` and implement it for `Person`
2. Create an instance for `List[T]` and `Option[T]`
3. Try to encode a `List[Option[T]]`

</v-clicks>

Syntax hints:

```scala
given Type with {}
given [A]: Type[A] with {}
given [A](using otherType[A]): Type[A] with {}
```

---
transition: fade
---

## Exercices solution

<v-switch>
<template #0-3>

```scala {1-3|5-10|12-14} {at:1}
// 1. TC definition
trait JsonEncoder[-T]:
  def encode(t: T): String

// 3. TC instance for Person
given JsonEncoder[Person] with
  def encode(person: Person): String =
    s"""{"name":"${person.name}",
        "age":${person.age},
        "address":"${person.address}"}"""

// 4. Redefine methods as extension methods
extension [T](t: T)(using encoder: JsonEncoder[T])
  def toJson: String = encoder.encode(t)
```

</template>
<template #3-6>

```scala {1-4|6-9|11-14} {at:4}
// TC instance for List[T]
given [T: JsonEncoder]: JsonEncoder[List[T]] with
  def encode(list: List[T]): String =
    list.map(_.toJson).mkString("[", ",", "]")

// TC instance for Option[T]
given [T: JsonEncoder]: JsonEncoder[Option[T]] with
  def encode(option: Option[T]): String =
    option.map(_.toJson).getOrElse("null")

List(Some(lulu), None, Some(zozo)).toJson
// [{"name":"lulu",...}
//  null
//  {"name":"zozo",...}]
```

</template>
</v-switch>

---
transition: fade
---

## Functor

```scala {1-2|4-7|9-11}
trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]

def mapThat[A, B, F[_]](fa: F[A])(f: A => B)(
  using functor: Functor[F]
): F[B] =
  functor.map(fa)(f)

mapThat(List(1, 2, 3))(_ + 1) // List(2, 3, 4)
mapThat(Option(1))(_ + 1) // Option(2)
mapThat(Try(1))(_ + 1) // Try(2)

// Allows to map over any generic type
```

---
transition: fade
---

## Monoid

```scala {1-3|5-6|8-9}
trait Monoid[A]:
  def empty: A
  def combine(a: A, b: A): A

def combineAll[A](fa: Seq[A])(using monoid: Monoid[A]): A =
  fa.foldLeft(monoid.empty)(monoid.combine)

combineAll(List(1, 2, 3)) // 6
combineAll(Seq("Scala", "is", "great")) // "Scala is great"

// Allows to combine any non-generic type
```

---
transition: fade
---

## Semigroupal

```scala {1-2|4-7|9-11}
trait Semigroupal[F[_]]:
  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]

def tupleAll[A, B, F[_]](fa: F[A], fb: F[B])(
  using sg: Semigroupal[F]
): F[(A, B)] =
  sg.product(fa, fb)

tupleAll(List(1, 2), List(4, 5)) // List((1, 4), (1, 5), (2, 4), (2, 5))
tupleAll(Option(1), Option(2)) // Option((1, 2))
tupleAll(Option(1), Option.empty[Int]) // None

// Allows to run a product on any generic type
```

---
transition: fade
---

## Semigroupal combinations

::div{.grid .grid-cols-2 .gap-4}

<div>

### Applicative

For non-monadic types:

```scala
Semigroupal[Future].product(
  Future.failed(new Exception("Boom")),
  Future.failed(new Exception("Bang"))
) // ~ Future("Boom", "Bang")
```

Allows error propagation.

</div>

<div v-click>

### Monadic

For monadic types:

```scala
Semigroupal[Future].product(
  Future.failed(new Exception("Boom")),
  Future.failed(new Exception("Bang"))
) // ~ Future("Boom")
```

Avoids unnecessary computation.

</div>

::

---
transition: fade
---

## Apply

```scala {1-2|9|11|4-7|13}
trait Apply[F[_]]:
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

def applyAll[A, B, W[_]](wf: W[A => B], wa: W[A])(
  using apply: Apply[W]
) =
  apply.ap(wf)(wa)

val (list1, list2) = (List(0, 1), List(5, 10, 15))

val adder: List[Int => Int] = list1.map(i1 => (i2: Int) => i1 + i2)

applyAll(adder, list2) // List(5, 10, 15, 6, 11, 16)
// Allows to apply independent effects
```

---
transition: fade
---

## Monad

```scala {1-2|4-7|9-10}
trait Monad[F[_]]:
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

def chain[A, B, F[_]: Monad](fa: F[A])(f: A => F[B])(
  using m: Monad[F]
): F[B] =
  m.flatMap(fa)(f)

chain(List(1, 2, 3))(i => List(i, i + 1)) // List(1, 2, 2, 3, 3, 4)
chain(Option(1))(i => Option(i + 1)) // Option(2)

// Allows to chain operation and stop on first failure
```

---
transition: fade
---

## Design from type class

<v-clicks>

- **Functor**: allow to change the parser's result value
- **Monoid**: combine parsers (AND / OR operation) or values
- **Semigroupal**: operate on the result of multiple parsers at once
- **Apply**: for a more convenient `.mapN` method
- **Monad**: select a parser based on a first parser's result (e.g. choose "parser1" if "def" or "parser2" if "val")
- **Lazy**: interesting things to do (later)

</v-clicks>

---
layout: section
transition: fade
---

# Parsing expressions

---
transition: fade
---

## Clone this repo

[github.com/iusildra/case-study-parser](https://github.com/iusildra/case-study-parser)

---
transition: fade
---

## Reification

1 abstract concept = 1 implementation and reification methods.

```scala {1-2|5-7,9|3-4,10-13}
trait Parser[+A]:
  def parse(input: String): Result[A] // reification method
  def map[B](f: A => B): Parser[B] = // reification method
    Map(this, f)
object Parser:
  def string(s: String): Parser[String] = // reification method
    ParserString(s)

case class ParserString(s: String) extends Parser[String] {}
case class Map[A, B](
  source: Parser[A],
  f: A => B
) extends Parser[B] {}
```

---
transition: fade
---

## Reification methods

With $\mathcal{P}$ the parser algebra & $\mathcal{A}$ the ADT algebra:

<v-clicks>

- **Constructors** $c$: $\forall x \notin \mathcal{P},\ c(x) \in \mathcal{P}$

  ```scala
  def string(s: String): Parser[String]
  ```

- **Combinators** $f$: $\forall c_1, c_2 \in \mathcal{P},\ f(c_1, c_2) \in \mathcal{P}$

  ```scala
  def orElse[A, B](left: Parser[A], right: Parser[B]): Parser[A | B]
  ```

- **Interpreters** $z$: $\forall c \in \mathcal{P},\ x \notin \mathcal{P},\ z(c, x) \in \mathcal{A}$

  ```scala
  def parse[A](in: String): Result[A]
  ```

</v-clicks>

---
transition: fade
---

## Base types

```scala {1-3|5-7|9-10}
sealed trait Parser[+A]:
  def parse(input: String): Result[A]
  protected def parse(input: String, index: Int): Result[A]

sealed trait Result[+A]:
  def map[B](f: A => B): Result[B]
  def orElse[B](that: Result[B]): Result[A | B]

case class Success[+A](...) extends Result[A]
case class Failure extends Result[Nothing]
```

---
transition: fade
---

## Parser map

```scala {3-6|1|7-8|10-11}
sealed trait Parser[+A] { def map[B](f: A => B): Parser[B] }

class Map[A, B](
  source: Parser[A],
  f: A => B
) extends Parser[B]:
  def parse(input: String, index: Int): Result[B] =
    source.parse(input, index).map(f)

case class Ident(value: String)
Parser.alphas.oneOrMore.combineAll.map(s => Ident(s))
```

---
transition: fade
---

## Parser alternative

```scala {4-7|1-2|8-11|13-15}
sealed trait Parser[+A]:
  def orElse[B](that: Parser[B]): Parser[A | B]

class OrElse[A, B](
  left: Parser[A],
  right: Parser[B]
) extends Parser[A | B]:
  def parse(input: String, index: Int): Result[A | B] =
    left
      .parse(input, index)
      .orElse(right.parse(input, index))

Parser.string("def").orElse(Parser.string("val"))
```

---
transition: fade
---

## Parser product

```scala {4-7|1-2|8-12|14-17}
sealed trait Parser[+A]:
  def product[B](that: Parser[B]): Parser[(A, B)]

class Product[A, B](
  l: Parser[A],
  r: Parser[B]
) extends Parser[(A, B)]:
  def parse(input: String, index: Int): Result[(A, B)] =
    l.parse(input, index) match
      case fail: Failure => fail
      case Success(left, _, offset) =>
        r.parse(input, offset).map((left, _))

parser1.product(parser2).product(parser3) // ((A, B), C)
```

---
transition: fade
---

## Apply instance

```scala {1-6|8-11}
object Parser:
  given parserApply: Apply[Parser] with
    def map[A, B](fa: Parser[A])(f: A => B): Parser[B] = fa.map(f)

    def ap[A, B](ff: Parser[A => B])(fa: Parser[A]): Parser[B] =
      ff.product(fa).map((f, a) => f(a))

(parser1, parser2, parser3).mapN {
  case (a, b, c) => ???
}
```

---
transition: fade
---

## flatMap implementation

```scala {3-6|1|7-11|13-15}
trait Parser[+A] { def flatMap[B](f: A => Parser[B]): Parser[B] }

class FlatMap[A, B](
  source: Parser[A],
  f: A => Parser[B]
) extends Parser[B]:
  def parse(input: String, index: Int): Result[B] =
    source.parse(input, index) match
      case fail: Failure => fail
      case Success(result, _, offset) =>
        f(result).parse(input, offset)

fieldParser.flatMap:
  case "def" => defParser
  case "val" => valParser
```

---
transition: fade
---

## Repeated (Kleene star)

```scala {5-7|1-3|8-11|13-17|19-20|22-25}
trait Parser[+A]:
  def zeroOrMore[B >: A]: KleeneStarParser[B] =
    KleeneStarParser(this)

class KleeneStarParser[A](
  parser: Parser[A]
) extends Parser[List[A]]:
  def rec(input: String, idx: Int, res: Queue[A]): (Int, Queue[A]) =
    parser.parse(input, idx) match
      case fail: Failure => (idx, res)
      case Success(r, _, offset) => rec(input, offset, res += r)

  def parse(input: String, index: Int): Result[List[A]] =
    rec(input, index) match
      case (_, Queue()) => Result.success(List(), input, index)
      case (offset, results) =>
        Result.success(results.toList, input, offset)

  def combineAll(using monoid: Monoid[A]): Parser[A] =
    this.map(_.foldLeft(monoid.empty)(monoid.combine))

  def combineWith[F[_]](empty: F[A])(
    combine: (F[A], A) => F[A]
  ): Parser[F[A]] =
    this.map(_.foldLeft(empty)(combine))
```

---
transition: fade
---

## Exercice: method parser

```scala
// def foo(x: Int, y: String): Boolean
case class Ident(value: String)
case class Param(name: Ident, tpe: Ident)
case class Method(name: Ident, params: Queue[Param], tpe: Ident)
```

```scala {1-10|12-14|16-23|25-30|31-33|35-45}
val ident = // \w+
  Parser.alphas.oneOrMore
    .combineAll.map(s => Ident(s))
val whitespace = // [ ]*
  Parser.chars(' ')
    .zeroOrMore.combineAll
val typeSep = // [ ]*:[ ]*
  whitespace *> Parser.char(':') <* whitespace
val paramSep = // [ ]*,[ ]*
  whitespace *> Parser.char(',') <* whitespace

val param = // <ident>[ ]*:[ ]*<ident>
  (ident <* typeSep, ident)
    .mapN { Param(_, _) }

val paramList = // (<param> . <paramSep>)*(<param>)?
  (param <* paramSep).zeroOrMore
    .combineWith(Queue.empty)(_ :+ _)
    .product(param.orElse(Parser.empty))
    .map {
      case (a, b: Param) => a :+ b
      case (a, null) => a
    }

val methodName = // "def <ident>"
  Parser.string("def ") *> ident
val methodParams = // "(<paramList>)"
  char('(') *> paramList <* char(')')
val methodType = // ":[ ]*<ident>"
  typeSep *> ident
val methodParser = // "def <ident>(<paramList>)<methodType>"
  (methodName, methodParams, methodType)
    .mapN { case (a, b, c) => Method(a, b, c) }

methodParser.parse("def foo(x: Int, y: Int): Int").get
/*
  Method(
    Ident(foo),
    Queue(
      Param(Ident(x), Ident(Int)),
      Param(Ident(y), Ident(Int))
    ),
    Ident(Int)
  )
*/
```

---
transition: fade
---

## Exercice: Binary expression

```scala {1-2,4-6,8-15|17-28}
sealed trait Expr:
  def +(that: Expr) = Add(this, that)

case class Literal(value: Int) extends Expr
case class Variable(name: String) extends Expr
case class Add(left: Expr, right: Expr) extends Expr

val nbr = // \d+
  Parser.digits.oneOrMore.combineAll.map(i => Literal(i))
val nme = // \w+
  Parser.chars.oneOrMore.combineAll.map(s => Variable(s))
val exp = // <nbr> | <nme>
  nbr.orElse(nme)
val add = // [ ]*\+[ ]*
  whitespace *> Parser.char('+') <* whitespace

val additionParser = (exp, add, exp).mapN { case (l, _, r) => l + r }
additionParser.parse("1+2+3").get // Add(1, 2)
```

---
transition: fade
---

## Recursive expressions

<v-switch>
<template #0>

```scala
val parser0 =
  (exp, add, exp)
    .mapN((l, _, r) => l + r)
```

Cannot parse recursive expressions.

</template>
<template #1>

```scala
val parser1: Parser[Add] =
  (exp, add, parser1.orElse(exp))
    .mapN((l, _, r) => l + r)
```

`NullPointerException`.

</template>
<template #2>

```scala
def parser2: Parser[Add] =
  (exp, add, parser2.orElse(exp))
    .mapN((l, _, r) => l + r)

parser2.parse("1+2+3")
```

`StackOverflowError!`

</template>
</v-switch>

---
transition: fade
---

## Delaying evaluation

```scala {4|1-2|5-7}
object Parser:
  def lzy[T](parser: => Parser[T]): Parser[T] = LazyParser(parser)

class LazyParser[A](source: => Parser[A]) extends Parser[A]:
  override def parse(input: String, index: Int): Result[A] =
    source.parse(input, index)
```

<v-click>

```scala {1-3|4-13}
def parser3: Parser[Add] =
  (exp, add, Parser.lzy(additionParser3).orElse(exp))
    .mapN((l, _, r) => l + r)
additionParser3.parse("1+2+3")
// Add(
//   Literal(1),
//   Add(Literal(2), Literal(3))
// )
```

</v-click>

---
layout: section
transition: fade
---

# Thoughts on performances

---
transition: fade
---

## Lots of overhead

::div{.grid .grid-cols-2 .gap-4}

<div>

### Example

```scala
Parser.alphas
// ParserOrElse(
//  initial = ParserOrElse(
//   initial = ParserOrElse(
//    ...
//     initial = ParserString(a),
//     recover = ParserString(b)
//    ...
//   )
//   recover = ParserString(y)
//  )
//  recover = ParserString(z)
// )
```

</div>

<div v-click="1">

### Comparison

```scala {3-6|8-9} {at:2}
val randomString = ???

parseWith(randomString): c =>
  (97 <= c && c <= 122)
  || (65 <= c && c <= 90)
// Takes <100ms

strParser.parse(randomString)
// Takes > 10s
```

Adding a `.charWhere(p: A => Boolean)` method is a solution.

</div>

::

---
transition: fade
---

## Unnecessary evaluations

::div{.grid .grid-cols-2 .gap-4}

<div>

### Example

```scala
val p =
  strParser.orElse(intParser)
// ParserOrElse(
//  initial = strParser
//  recover = intParser
// )
p.parse("a string")
```

`intParser` evaluation is forced, even if we don't need it.

</div>

<div v-click>

### Solution

```scala {3,8}
sealed trait Parser[+A]:
  def orElse[B](
    that: => Parser[B]
  ): Parser[A | B]

class ParserOrElse[A, B](
  left: Parser[A],
  right: => Parser[B]
) extends Parser[A | B]:
```

</div>

::

---
transition: fade
---

## Unnecessary (re)evaluations

::div{.grid .grid-cols-2 .gap-4}

<div>

### Example

```scala
class LazyParser[A](
  source: => Parser[A]
) extends Parser[A]:
  override def parse(...) =
    source.parse(...)
```

`source` is recomputed each time the `.parse` method is called.

</div>

<div v-click>

### Solution

```scala {4,6}
class LazyParser[A](
  source: => Parser[A]
) extends Parser[A]:
  lazy val cached = source
  override def parse(...) =
    cachedParser.parse(...)
```

Also called "memoization", or call-by-need.

</div>

::

---
layout: statement
class: text-center
---

# Questions ?

---
layout: end
hideInToc: true
---

# Thanks for your attention!

And your patience
