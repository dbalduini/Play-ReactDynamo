package play.modules.reactdynamo

import scala.language.experimental.macros

object Dynamo {

  implicit def format[A] = macro TableMacroImpl.formatImpl[A]

}
