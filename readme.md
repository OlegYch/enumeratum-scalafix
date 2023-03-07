[![enumeratum-scalafix Scala version support](https://index.scala-lang.org/olegych/enumeratum-scalafix/enumeratum-scalafix/latest-by-scala-version.svg)](https://index.scala-lang.org/olegych/enumeratum-scalafix/enumeratum-scalafix)

# Scalafix migration from scala.Enumeration to enumeratum.

Supports enums with ids and custom names.

Doesn't support enums with custom value types.

Note: comments and custom formatting inside Enumerations will be removed.

To run:
```
sbt 
scalafixAll dependency:EnumeratumScalafix@io.github.olegych:enumeratum-scalafix:VERSION
```
