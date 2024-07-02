val o1 = Option(1)
val o2 = Option(2)
val o3 = None

// 1.
val increment = (o: Option[Int]) => o.map(_ + 1)
increment(o1)
increment(o3)

// 2.
val doubleIfEven = (o: Option[Int]) => o.filter(_ % 2 == 0).map(_ * 2)
doubleIfEven(o1)
doubleIfEven(o2)
doubleIfEven(o3)

//3.
val fallback = (o1: Option[Int], o2: Option[Int]) =>
  o1
    .flatMap: a =>
      o2.map: b =>
        a + b

fallback(o1, o2)
