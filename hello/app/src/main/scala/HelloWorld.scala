object HelloWorld extends App {
  import LibraryMacros._

  object Enum1 extends Enumeration {
    val T01, T02, T03 = Value
  }

  object Enum2 extends Enumeration {
    val t01, t02, t03 = Value
  }

  val a = greeting(Enum1, Enum2)

  println(a.from1(Enum1.T01))
}
