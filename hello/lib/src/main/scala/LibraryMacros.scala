import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object LibraryMacros {
  def greeting[E1 <: Enumeration, E2 <: Enumeration]: Converter[E1#Value, E2#Value] = macro greetingMacro[E1, E2]

  def greetingMacro[E1 <: Enumeration, E2 <: Enumeration](c: blackbox.Context)(implicit E1: c.WeakTypeTag[E1], E2: c.WeakTypeTag[E2]): c.Expr[Converter[E1#Value, E2#Value]] = {
    import c.universe._

    def terms[T](wt: c.WeakTypeTag[T]) = wt.tpe.decls.sorted collect {
      case t: TermSymbol if t.isPublic && !t.isConstructor => q"$wt.${t.name}"
    }

    print(terms(E1))

    val a = (terms(E1) zip terms(E2)) map {
      case (e1: Tree, e2: Tree) => (q"$e1 -> $e2", q"$e2 -> $e1")
    }

    val (f1, f2) = a.unzip

    val b = reify(Seq.empty[(E1#Value, E2#Value)])

    val f11 = f1.foldRight(b){
      case (t, xs) => q"$xs :+ $t"
    }

    print(b)

    c.Expr[Converter[E1#Value, E2#Value]](q"Converter(Nil, Nil)")
  }
}

trait Converter[E1 <: Enumeration#Value, E2 <: Enumeration#Value] {
  def from1(e: E1): E2
  def from2(e: E2): E1
}

object Converter {
  def apply[E1 <: Enumeration#Value, E2 <: Enumeration#Value](s1: Seq[(E1, E2)], s2: Seq[(E2, E1)]): Converter[E1, E2] = new Converter[E1, E2] {
    val m1 = s1.toMap
    val m2 = s2.toMap

    override def from1(e: E1): E2 = m1(e)
    override def from2(e: E2): E1 = m2(e)
  }
}
