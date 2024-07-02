---
layout: section
transition: fade
---

# Functional Programming

---
transition: fade
---

## Global view

<v-clicks>

- No instructions, only expressions
- Functions as 1<sup>st</sup> class citizens
- Immutability
- Referential transparency
- Pattern matching
- Recursion over loops
- Lazy evaluation

</v-clicks>

---
transition: fade
---

## Functions are first class citizens

Functions are values

````md magic-move {lines: true}

```scala
val square: Int => Int =
  (x: Int) => x * x
```

```scala
val add2: Int => Int =
  x => x + 2
```

```scala
val add2: Int => Int =
  _ + 2
```

````

<div v-click>

They can be assigned to values or passed as arguments

```scala
def apply(f: Int => Int, x: Int): Int = f(x)
val square = (x: Int) => x * x
apply(square, 2) // 4
```

</div>

<div v-click>

They can be returned from functions

```scala
def makeAdder(x: Int): Int => Int = (y: Int) => x + y
val add2 = makeAdder(2)
add2(3) // 5
```

</div>

---
transition: fade
---

## Composition

Composition in Mathematics...

$$
\begin{align*}
  f: A \mapsto B \\
  g: B \mapsto C \\
  g \circ f: A \mapsto C
\end{align*}
$$

...and in programming. It's the same!

```scala {none|1-4|5-6|7-8|all}
val square: Int => Int =
  (x: Int) => x * x
val doubledString: Int => String =
  (x: Int) => s"Double of $x is ${x * 2}"
val squareAndDouble: Int => String =
  (x: Int) => doubledString(square(x))
squareAndDouble(5)
// res0: String = Double of 25 is 50
```

---
layout: section
transition: fade
---

## Exercices --- Composition

---
transition: fade
---

### Exercices --- Composition

Given:
```scala
val double: Int => Int = x => x * 2
val addOne: Int => Int = x => x + 1
val toString: Int => String = x => s"Result: $x"
```

1. 🟢 Compose `double` and `addOne` to create `doubleAndAdd: Int => Int` that doubles then adds one
2. 🟢 Compose all three to create `pipeline: Int => String`
3. 🟡 Write a generic `compose[A, B, C](f: B => C, g: A => B): A => C` function
4. 🔴 Write `pipe[A, B, C](f: A => B, g: B => C): A => C` — same as compose but with reversed argument order. Which feels more natural?
5. 🔴 Write `andThen` as an extension method: `extension [A, B](f: A => B) def andThen[C](g: B => C): A => C`

<v-click>

```scala
// Scala provides compose and andThen on Function1
val f = double.andThen(addOne)  // double first, then addOne
val g = double.compose(addOne)  // addOne first, then double
```

</v-click>

---
title: Curry(troll)
level: 2
hideInToc: true
layout: image
image: /curry.webp
transition: fade
---

---

## Curry(ing)

<v-click>

$f: (A, B, C) \mapsto D \longrightarrow f': f_1 \circ f_2 \circ f_3$

</v-click>

<Anim spec="@code [1-2|3-5|6-7|8-]">

```scala
def multipleAdder_1(v1: Int, v2: Int, v3: Int): Int =
  v1 + v2 + v3
multipleAdder_1(1, 2, 3) // 6
multipleAdder_1(1, 2, 5) // 8

val multipleAdder_2: Int => Int => Int => Int =
  (v1) => (v2) => (v3) => v1 + v2 + v3
val add3 = multipleAdder_2(1)(2)
add3(3) // 6
add3(5) // 8
```

</Anim>

<div v-click at="2">
Use cases ?

- Avoid repeating the same values / Keeping the context
- Cache (long) computation results without mutation / leaks
- Chaining function calls in pipeline

</div>

---
transition: fade
---

## Immutability pros and cons ?

<v-clicks>

Pros:

- Thread-safe by default (no locks needed)
- Easier to reason about (no hidden state changes)
- Enables structural sharing (efficient memory)
- Makes testing trivial (same input = same output)
- Enables safe parallelization
- No defensive copying needed

Cons:

- Learning curve for developers used to mutation
- Require more memory without structural sharing
- Some algorithms are naturally expressed with mutation
- Performance overhead in naive implementations

</v-clicks>

---
transition: fade
---

## Immutability

<v-click>

A stateful program is an un-deterministic program

- Race-conditions
- Concurrent accesses => locks
- Nothing can guarantee the correctness of your inputs at any time

</v-click>

<v-click>

A stateless program does not have the same issues

</v-click>

---
transition: fade
---

## Referential Transparency (RT)

A method:

<v-clicks>

- that has no side-effect
- whose output depends only on its input

</v-clicks>

<v-click>

is <u>referentially transparent</u> and can be replaced by its value <u>without</u> altering the program

</v-click>

<v-click>

Which method is referential transparent ?

```scala
def add(x: Int, y: Int): Int = x + y
def mutateState(x: Int): Unit = this.count = x
def querySql(s: String): X = db.query(s)
def apply[T, U](f: T => U)(x: T): U = f(x)
```

</v-click>

<v-clicks>

- `add`: ✅
- `mutateState`: ❌
- `querySql`: ❌
- `apply`: 🤷

</v-clicks>

---
transition: fade
---

### Advantages

<v-clicks>

- Easier to reason about (local reasoning)
- Easier to test
  - No complicate setup
  - No need for mocking (that might hide bugs)
  - Faster tests
- Easier to refactor
- Easier to parallelize

</v-clicks>

---
transition: fade
---

## Recursion over loops <span v-click="4">--- tail recursion</span>

A loop is an <u>instruction</u>, thus can't be assigned to a value

```scala
val rabbit = while (???) {???} // WTF ?
```

<v-click>
Classic example of factorial

```scala
def fact(n: Int) = n match
  case 0 | 1 => n
  case _     => n * fact(n - 1)
```

</v-click>

<v-clicks>

Problem ? Try it out with a big number like `1e9`

```java
java.lang.StackOverflowError
```

Solution ? Tail recursion

````md magic-move

```scala
def loop(i: Int, acc: Int): Int = i match
  case 0 | 1 => acc
  case _     => loop(i - 1, i * acc)
```

```scala
def fact(n: Int): Int =
  def loop(i: Int, acc: Int): Int = i match
    case 0 | 1 => acc
    case _     => loop(i - 1, i * acc)
  loop(n, 1)
```

````

</v-clicks>

<!--
Demander s'ils arrivent à déterminer ce qui fait qu'une fonction est tail-recursive ou non
-->

---
transition: fade
---

## Tail recursion --- fibonacci

Write the fibonacci method in tail-recursion. You can use the `@tailrec` annotation on a method to have the compiler tells you when it cannot be optimized to a tail-rec call.

<v-click>

````md magic-move

```scala
def loop(i: Int, a: Int, b: Int): Int =
  if i >= n then a
  else loop(i + 1, a + b, a)
```

```scala
// You can use the @tailrec annotation to force the compiler to check if the method is tail-rec
def fibo(n: Int): Int =
  def loop(i: Int, a: Int, b: Int): Int =
    if i >= n then a
    else loop(i + 1, a + b, a)
  loop(2, 1, 1)
```

````

</v-click>

<v-clicks>

Bonus: compare the average execution time between this version and the non tail-rec one with `n = 30` 😉

So, what makes a method tail recursive ?

$\Rightarrow$ The recursive call must be the last operation of the method

</v-clicks>

---
layout: section
transition: fade
---

## Exercices --- Recursion

---
transition: fade
---

### Exercices --- Tail Recursion

Implement the following using tail recursion (use `@tailrec` annotation to verify):

1. 🟢 `sum(xs: List[Int]): Int` — sum all elements
2. 🟢 `length[A](xs: List[A]): Int` — count elements without using `.length`
3. 🟡 `reverse[A](xs: List[A]): List[A]` — reverse a list
4. 🔴 `flatten[A](xs: List[List[A]]): List[A]` — flatten nested lists
5. 💀 `foldLeft[A, B](xs: List[A], z: B)(f: (B, A) => B): B` — implement foldLeft from scratch (reduce the whole list to a single result, e.g sum of all elements, product of all elements, etc.)

<v-click>

```scala
// Pattern for tail recursion
def myFunc[A](xs: List[A]): Result =
  @tailrec
  def loop(remaining: List[A], acc: Result): Result = remaining match
    case Nil     => acc
    case x :: xs => loop(xs, /* update acc */)
  loop(xs, /* initial accumulator */)
```

</v-click>

---
transition: fade
---

## Lazy evaluation

Do not evaluate an expression when it's declared, but when it's required

```scala
val x = 42            // x: Int = 42
lazy val y = "Rabbit" // y: String
```

<v-click>
Usages ?

- Control flows
- Infinite sequences
- Avoid evaluation of unnecessary/expensive expressions
- ...

</v-click>

```scala {hide|3|5|6|all}
// For the context
trait Option[+A]:
  def orElse[B >: A](alternative: => Option[B]): Option[B]

val myUser: User | Null = User("Alice")
Option(myUser).orElse(findUserFromDb).map(_.name) // findUserFromDb is never evaluated
```

---
transition: fade
---

## Lazy evaluation --- by-value | by-name | by-need

There are 3 ways to declare the order of evaluation of an expression

````md magic-move

```scala
def evaluateByValue(x: Int) =
  for _ <- 1 to 3 yield x

evaluateByValue(Random.nextInt(10)) // 3 times the same value
```
```scala {3,4,7}
def evaluateByValue(x: Int) =
  for _ <- 1 to 3 yield x
def evaluateByName(x: => Int) =
  for _ <- 1 to 3 yield x

evaluateByValue(Random.nextInt(10)) // 3 times the same value
evaluateByName(Random.nextInt(10))  // 3 potentially different values
```

```scala {5-7,11}
def evaluateByValue(x: Int) =
  for _ <- 1 to 3 yield x
def evaluateByName(x: => Int) =
  for _ <- 1 to 3 yield x
def evaluateByNeed(x: => Int) =
  lazy val cache = x
  for _ <- 1 to 3 yield cache

evaluateByValue(Random.nextInt(10)) // 3 times the same value
evaluateByName(Random.nextInt(10))  // 3 potentially different values
evaluateByNeed(Random.nextInt(10))  // 3 times the same value
```

````

<v-click at="3">

Usage of delayed evaluation ? Build meta-programs !

</v-click>

<v-click at="4">

```scala {hide|hide|1|1-2|1-3|1-4}
getUserFromCache(id)            // Option[User]
  .orElse(getUserFromDb(id))    // Option[User]
  .map(_.preferences)           // Option[List[String]]
  .exists(_.contains("Scala"))  // Boolean
```

</v-click>

---
layout: section
transition: fade
---

## Exercices --- Lazy Evaluation

---
transition: fade
---

### Exercices --- Lazy Evaluation

1. 🟢 Define `lazy val x` that prints `"computing"` and returns `42`. Call it twice. How many times is `"computing"` printed?
2. 🟡 Write `getOrElse[A]` taking an `Option[A]` and a default value producer — the default should only be evaluated if option is empty
3. 🔴 Implement `memoize[A, B](f: A => B): A => B` that caches results (hint: use a mutable Map internally — this is acceptable for memoization)
4. 💀 Create a `LazyList` that generates infinite natural numbers. Implement `take(n: Int): List[Int]`

  ```scala
  // Hint for 4
  class LazyList[A](headVal: A, tailFn: => LazyList[A]):
    lazy val head: A = headVal
    lazy val tail: LazyList[A] = tailFn
  ```

<v-click>

```scala
// Expected behavior for 3
val slowFib: Int => BigInt = ???
val fastFib = memoize(slowFib)
fastFib(40) // slow first time
fastFib(40) // instant second time
```

</v-click>

---
transition: fade
---

## Monads

Monads are monoid in the category of endofunctors 🤓

Very abstract, but powerful!

Monoid = identity element + associative binary operation (combine)

- $\mathbb{N}$: identity element `0`, associative binary operation `+`
- $\mathbb{N}$: identity element `1`, associative binary operation `*`
- `Option`: identity element `None`, associative binary operation `flatMap`

---
transition: fade
---

## Monads — in practice

Rules:

- Associativity: `m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))`
- Left identity: `Option(value).flatMap(f) == f(value)`
- Right identity: `m.flatMap(Option(_)) == m`