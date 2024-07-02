val double = (x: Int) => x * 2
val addOne = (x: Int) => x + 1
val asString = (x: Int) => s"Result: $x"

val doubleAndAdd: Int => Int = x => addOne(double(x))

doubleAndAdd(3)

val pipeline: Int => String = x => asString(addOne(double(x)))
pipeline(3)