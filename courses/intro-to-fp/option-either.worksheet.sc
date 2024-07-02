object Chain {
  def findUser(id: String): Option[User] = ???
  def getEmail(u: User): Option[String] = ???

  findUser("123").flatMap(getEmail).getOrElse("no-email")

  for
    user <- findUser("123")
    email <- getEmail(user)
  yield s"$user with email $email"
}

case class User(email: Option[String])

def findUser(id: String): Option[User] =
  if (id == "123") Some(User(Some("alice@alice.com")))
  else None

def getEmail(u: User): Option[String] = u.email

object OptionExample {
  findUser("123").flatMap(getEmail).getOrElse("no-email")

  for
    user <- findUser("123")
    email <- getEmail(user)
  yield email
}

enum BusinessError:
  case UserNotFound(id: String)
  case WrongPassword
  case AccountLocked(userId: String)

def login(username: String, password: String): Either[BusinessError, User] =
  Left(BusinessError.WrongPassword)

login("alice", "wrongpassword") match
  case Left(BusinessError.UserNotFound(id)) => s"User with id $id not found"
  case Left(BusinessError.WrongPassword)    => "Wrong password"
  case Left(BusinessError.AccountLocked(userId)) =>
    s"Account for user $userId is locked, please contact support"
  case Right(value) => s"Welcome ${value.email.getOrElse("user")}"












def sequence[A](options: List[Option[A]]): Option[List[A]] =
  options.foldRight(Option(List.empty[A])) {
    for {
      x <- _
      xs <- _
    } yield x :: xs
  }
def sequence2[A](options: List[Option[A]]): Option[List[A]] =
  options.foldRight(Option(List.empty[A])) { case (value, acc) =>
    for {
      x <- value
      xs <- acc
    } yield x :: xs
  }
def sequence3[A](options: List[Option[A]]): Option[List[A]] =
  options.foldRight(Option(List.empty[A])) {
    case (None, _) | (_, None) => None
    case (Some(x), Some(xs))   => Some(x :: xs)
  }

sequence(List(Some(1), Some(2), Some(3)))
sequence(List(Some(1), None, Some(3)))
sequence(List(None, None, None))
sequence(List(None, Some(1)))
sequence(List(Some(1), None))
