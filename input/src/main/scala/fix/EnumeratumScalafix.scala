/*
rule = EnumeratumScalafix
 */
package fix

object A extends Enumeration {
  val X  = Value(0)
  val Y  = Value("x")
  val Z  = Value(2, "Zed")
  val ZZ = Value("ZZ")

  val a = 1
  val z = values

  def x() = 1 // hello
  x()
}

object NoId extends Enumeration {
  val A, B, C = Value
}
