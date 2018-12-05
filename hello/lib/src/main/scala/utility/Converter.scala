package utility

/**
  * created by gigitsu on 12/10/18.
  */
trait Converter[E1 <: Enumeration#Value, E2 <: Enumeration#Value] {
  val m1: Map[E1, E2]
  val m2: Map[E2, E1]

  def from1(e: E1): E2 = m1(e)
  def from2(e: E2): E1 = m2(e)
}
