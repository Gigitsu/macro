import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object LibraryMacros {
  def greeting[E1 <: Enumeration, E2 <: Enumeration](e1: E1, e2: E2): Converter[E1#Value, E2#Value] = macro greetingMacro[E1, E2]

  def greetingMacro[E1 <: Enumeration, E2 <: Enumeration](c: blackbox.Context)(e1: c.Expr[E1], e2: c.Expr[E2])(implicit E1: c.WeakTypeTag[E1], E2: c.WeakTypeTag[E2]): c.Expr[Converter[E1#Value, E2#Value]] = {
    import c.universe._

    val converter = TermName(weakTypeOf[Converter.type].typeSymbol.name.decodedName.toString.stripSuffix(".type"))

    def terms[T](wt: c.WeakTypeTag[T], e: c.Expr[T]) = wt.tpe.decls.sorted collect {
      case t: TermSymbol if t.isPublic && !t.isConstructor => q"$e.${t.name}"
    }

    val a = (terms(E1, e1) zip terms(E2, e2)) map {
      case (e1: Tree, e2: Tree) => (q"$e1 -> $e2", q"$e2 -> $e1")
    }

    val (f1, f2) = a.unzip

    val tree = q"$converter(Map(..$f1), Map(..$f2))"

    c.Expr[Converter[E1#Value, E2#Value]](tree)
  }
}

trait Converter[E1 <: Enumeration#Value, E2 <: Enumeration#Value] {
  def from1(e: E1): E2
  def from2(e: E2): E1
}

object Converter {
  def apply[E1 <: Enumeration#Value, E2 <: Enumeration#Value](m1: Map[E1, E2], m2: Map[E2, E1]): Converter[E1, E2] = new Converter[E1, E2] {
    override def from1(e: E1): E2 = m1(e)
    override def from2(e: E2): E1 = m2(e)
  }
}
