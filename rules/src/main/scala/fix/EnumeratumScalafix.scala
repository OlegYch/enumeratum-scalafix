package fix

import scalafix.v1._

import scala.meta._

class EnumeratumScalafix extends SemanticRule("EnumeratumScalafix") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case o: Defn.Object
          if o.templ.inits.exists(_.tpe.symbol.value == "scala/Enumeration#") =>
        case class enum(
            name: String,
            nameOverride: Option[String],
            id: Option[Int]
        )
        val enums       = for {
          t <- o.templ.stats.map {
                 case t: Defn.Val =>
                   def values(a: Tree, args: List[Term]) = t.pats.collect {
                     case pat: Pat.Var =>
                       val e = args match {
                         case List(Lit.Int(int))                     =>
                           enum(pat.name.value, None, Some(int))
                         case List(Lit.Int(int), Lit.String(string)) =>
                           enum(pat.name.value, Some(string), Some(int))
                         case List(Lit.String(string))               =>
                           enum(pat.name.value, Some(string), None)
                         case List()                                 => enum(pat.name.value, None, None)
                       }
                       Some(e) -> t
                   }

                   t.rhs match {
                     case a @ Term.Apply(Term.Name(name), _)
                         if name == "Value" =>
                       values(a, a.args)
                     case a @ Term.Name(name) if name == "Value" =>
                       values(a, Nil)
                     case _                                      => List(None -> t)
                   }
                 case t           => List(None -> t)
               }
          t <- t
        } yield t
        val withId      = enums.exists(_._1.exists(_.id.nonEmpty))
        val enumPatches = enums
          .collect { case (Some(e), a) =>
            e -> a
          }
          .zipWithIndex
          .map { case ((e, a), idx) =>
            val id   = if (withId) s"(${e.id.getOrElse(idx)})" else ""
            val code =
              s"  case object ${e.name} extends ${o.name.value}$id" + e.nameOverride
                .filter(_ != e.name)
                .map { name =>
                  " {\n    override val entryName = \"" + name + "\"\n  }\n"
                }
                .getOrElse("\n")
            Patch.addRight(a, code)
          }
        val bodyPatches = enums.collect { case (None, t) =>
          Patch.addRight(t, t.syntax + "\n")
        }
        val classDef    =
          if (withId) s"abstract class ${o.name}(val id: Int)"
          else s"trait ${o.name}"
        val idMap       =
          if (withId) "  lazy val byId = values.map(v => v.id -> v).toMap\n  def apply(id: Int) = byId(id)\n"
          else ""
        Patch.addLeft(o, s"sealed $classDef extends enumeratum.EnumEntry\n") +
          Patch.replaceTree(
            o,
            s"object ${o.name} extends enumeratum.Enum[${o.name}] {\n  type Value = ${o.name}\n${idMap}  lazy val values = findValues\n\n"
          ) ++
          bodyPatches ++
          enumPatches +
          Patch.addRight(o, "}")
    }.asPatch
  }

}
