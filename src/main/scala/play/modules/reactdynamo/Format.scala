package play.modules.reactdynamo;


import scala.reflect.macros.Context
import scala.language.experimental.macros

trait Format[T] {
  def writes(t: T): Seq[(String, Any)]
  def reads(attributes: Seq[awscala.dynamodbv2.Attribute]): T
}

object TableMacroImpl {

  def formatImpl[A: c.WeakTypeTag](c: Context): c.Expr[Format[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]
    val declarations = tpe.declarations
    val ctor = declarations.collectFirst { case m: MethodSymbol if m.isPrimaryConstructor => m}.get
    val fields = ctor.paramss.head

    val writeParams: List[Tree] = fields.map { field =>
        val name = field.name.toTermName
        val mapKey: String = name.decoded
        q"""$mapKey -> t.$name"""
    }

    val companion = tpe.typeSymbol.companionSymbol

    def returnType(name: Name) = tpe.declaration(name).typeSignature

    //TODO REFATORAR
    val readParams = fields.map { field =>
      val name = field.name.toTermName
      val decoded = name.decoded
      val implType = field.typeSignature
      implType match {
        case t if implType =:= typeOf[String] =>
          println(t)
          q"""attr($decoded).s.getOrElse("")"""
        case t if implType =:= typeOf[Int] =>
          println(t)
          q"""attr($decoded).n.getOrElse("0").toInt"""
        case TypeRef(_, t, args) =>
          q"""Erro1"""
        case TypeRef(_, t, _) =>
          q"""Erro2"""
      }
    }

    c.Expr[Format[A]] { q"""
      new Format[$tpe] {
        def writes(t: $tpe) = Seq(..$writeParams)
        def reads(attributes: Seq[awscala.dynamodbv2.Attribute]): $tpe = {
          val attr = attributes.map(a => (a.name -> a.value)).toMap
          $companion(..$readParams)
        }
      }"""
    }

  }

}