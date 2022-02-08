package fix

sealed abstract class A(val id: Int) extends enumeratum.EnumEntry
object A extends enumeratum.Enum[A] {
  type Value = A
  lazy val byId = values.map(v => v.id -> v).toMap
  def apply(id: Int) = byId(id)
  lazy val values = findValues

  case object X extends A(0)
  case object Y extends A(1) {
    override val entryName = "x"
  }
  case object Z extends A(2) {
    override val entryName = "Zed"
  }
  case object ZZ extends A(3)
val a = 1
val z = values
def x() = 1
x()
}

sealed trait NoId extends enumeratum.EnumEntry
object NoId extends enumeratum.Enum[NoId] {
  type Value = NoId
  lazy val values = findValues

  case object A extends NoId
  case object B extends NoId
  case object C extends NoId
}
