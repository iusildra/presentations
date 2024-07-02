---
layout: section
transition: fade
---

# Error handling

---
layout: image
image: /dog-lies.png
backgroundSize: contain
transition: fade
---

---
transition: fade
---

## Don't lie about your return type

When you call

```scala
def getUser(id: Int): User
```

You might not want `java.lang.NullPointerException` because of a forgotten null-check

<v-click>

A better solution would be at least to return a `User | Null`. But wrapping your result an ADT like an `Option` is even better

You can easily define your logic by chaining methods while being able to act on every possible case, without the risk of failure

</v-click>

---
transition: fade
---

## Handle errors with `null` values --- `Option`

The `Option` type is a good way to nullable values in Scala

````md magic-move

```scala
sealed trait Option[+T]
case class Some[+T](value: T) extends Option[T]:
  def get: T = value
case object None extends Option[Nothing]:
  def get = throw new NoSuchElementException("None.get")
```

```scala
sealed trait Option[+T]
case class Some[+T](value: T) extends Option[T]
case object None extends Option[Nothing]
```

````

<v-click>

For instance if we want to compare 2 users' preferences:

````md magic-move

```scala
def getUserFromCache(id: Int): Option[User]
def getUserFromDb(id: Int): Option[User]
def getUserPreferences(id: Int): Option[List[String]]
def scorePreferences(prefs1: List[String], prefs2: List[String]): Double = 0.5
```

```scala
getUserFromCache(id1).orElse(getUserFromDb(id1))      // Option[User] 
  .flatMap { (user1: User) =>
    getUserFromCache(id2).orElse(getUserFromDb(id2))  // Option[User] 
      .flatMap { (user2: User) =>
        getUserPreferences(user1.id)                  // Option[List[String]]
          .flatMap { (prefs1: List[String]) =>
            getUserPreferences(user2.id)              // Option[List[String]]
              .map { (prefs2: List[String]) =>
                scorePreferences(prefs1, prefs2)      // 0.0 to 1.0
              }
          }
      }
  }
// res: Option[Double]
// 🤮
```

```scala {all}
for
  user1: User         <- getUserFromCache(id1).orElse(getUserFromDb(id1)) // Option[User] 
  user2: User         <- getUserFromCache(id2).orElse(getUserFromDb(id2)) // Option[User] 
  prefs1: List[String] <- getUserPreferences(user1.id) // Option[List[String]]
  prefs2: List[String] <- getUserPreferences(user2.id) // Option[List[String]]
yield scorePreferences(prefs1, prefs2)
// res: Option[Double]
```

```scala
// Even better, we don't need to chain calls between each user, they're independent
for
  (user1, user2) <- getUserFromCache(id1).orElse(getUserFromDb(id1))
                      .zip(getUserFromCache(id2).orElse(getUserFromDb(id2)))
  (pref1, pref2) <- getUserPreferences(user1.id)
                      .zip(getUserPreferences(user2.id))
yield scorePreferences(pref1, pref2)
// res: Option[Double]
```

````

</v-click>

<v-click>

- The `for-yield` (also called `for-comprehension`) is a syntactic sugar that allows to chain calls
- Each line with a `<-` is a `flatMap` call
  - Except the last one which is a `map` call

</v-click>

---
transition: fade
---

## Monadic operations

> A monad is a monoid in the category of endofunctors 🤓

<v-clicks>

- A monoid is a structure with an associative combine operation and an identity (neutral) element
- An endofunctor is a functor that maps a category to itself

</v-clicks>

<v-click>

Adapted to the `Option` type:

```scala
trait Option[+T]:
  def map[U](f: T => U): Option[U]
  def flatMap[U](f: T => Option[U]): Option[U] // combine operation (also called bind)
object Option:
  def apply[T](value: T): Option[T] = Some(value)

case class Some[+T](value: T) extends Option[T]
case object None extends Option[Nothing] // neutral element
```

</v-click>

<v-click>

<!-- There are some properties that must be respected for a monad:

- Associativity: `m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))`
- Left identity: `Option(value).flatMap(f) == f(value)`
- Right identity: `m.flatMap(Option(_)) == m` -->

</v-click>

---
transition: fade
---

## Handle errors with exceptions --- `Try`

The `Try` type is a good way to handle exceptions in Scala and has the same behavior as `Option`, but for exceptions

```scala
sealed trait Try[+T]
case class Success[+T](value: T) extends Try[T]
  def get: T = value
case class Failure[+T](exception: Throwable) extends Try[T]
  def get: T = throw exception
```

<v-click>

This way we can easily handle exceptions and recovery with reduced boilerplate/nesting and avoid mutations

```scala
def getUserFromCache(id: Int): Try[User]
def getUserFromDb(id: Int): Try[User]

def mayFail(user: User): Try[Unit] = ???
def mayFail2(user: User): Try[Unit] = ???

def foo: Try[Unit] =
  val user = getUserFromCache(id).orElse(getUserFromDb(id))

  user.flatMap: user =>
    mayFail(user).orElse(mayFail2(user))
```

</v-click>

---
transition: fade
---

## Generalized error handling --- `Either`

`Try` and `Option` are good for simple cases, but sometimes we need richer errors to fine-tune our logic

<v-click>

The `Either` type allows to wrap both the type of the value and the error, so we can use our own error types, without using exceptions.

</v-click>

<v-click>

```scala
sealed trait Either[+E, +T]:
case class Left [+E, +T](error: E) extends Either[E, T]
case class Right[+E, +T](value: T) extends Either[E, T]

sealed trait MyError
case class DuplicatedItem(item: Item) extends MyError
case class NonExistingItem(item: Item) extends MyError
case object Timeout

def validateCart(cart: Cart): Either[MyError, Cart] = ???

validateCart(cart) match {
  case Right(cart) => println("Cart is valid")
  case Left(DuplicatedItem(item)) => println(s"Item $item is duplicated")
  case Left(NonExistingItem(item)) => println(s"Item $item does not exist")
  case Left(Timeout) => println("Timeout, please retry")
}
```

</v-click>

---
transition: fade
---

## DevOps Example --- Config Validation

A real-world scenario: validating deployment configuration

```scala
case class DeployConfig(replicas: Int, port: Int, image: String)

sealed trait ConfigError
case class InvalidReplicas(n: Int) extends ConfigError
case class InvalidPort(p: Int) extends ConfigError
case object MissingImage extends ConfigError
```

<v-click>

```scala
def validateReplicas(n: Int): Either[ConfigError, Int] =
  if n > 0 && n <= 10 then Right(n) else Left(InvalidReplicas(n))

def validatePort(p: Int): Either[ConfigError, Int] =
  if p >= 1024 && p <= 65535 then Right(p) else Left(InvalidPort(p))

def validateImage(img: String): Either[ConfigError, String] =
  if img.nonEmpty then Right(img) else Left(MissingImage)

def validateConfig(r: Int, p: Int, img: String): Either[ConfigError, DeployConfig] =
  for
    replicas <- validateReplicas(r)
    port     <- validatePort(p)
    image    <- validateImage(img)
  yield DeployConfig(replicas, port, image)
```

</v-click>

---
layout: section
transition: fade
---

## Exercices --- Error Handling

---
transition: fade
---

### Exercices --- Option & Either

1. 🟢 Given `def findUser(id: Int): Option[User]`, chain a call to `def getEmail(u: User): Option[String]` and provide a default `"no-email"`
2. 🟡 Rewrite using for-comprehension:
   ```scala
   findUser(1).flatMap(u => getEmail(u).map(e => s"${u.name}: $e"))
   ```
3. 🔴 Model a login system with `Either`:
   - Define errors: `UserNotFound`, `WrongPassword`, `AccountLocked`
   - Write `login(username: String, password: String): Either[LoginError, User]`
   - Handle each error case with pattern matching
4. 💀 Implement `sequence`: `List[Option[A]] => Option[List[A]]` that returns `Some` only if all elements are `Some`

<v-click>

```scala
// Hint for 4
def sequence[A](opts: List[Option[A]]): Option[List[A]] =
  opts.foldRight(???) { (opt, acc) => ??? }
```

</v-click>

