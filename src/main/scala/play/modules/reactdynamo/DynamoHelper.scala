package play.modules.reactdynamo

import awscala.dynamodbv2.{DynamoDB, Attribute}

object DynamoHelper {
  implicit lazy val dynamoDB = DynamoDB.local()

  def toTable[E <: Entity](t: E)(implicit format: Format[E]) = format.writes(t)

  def fromTable[E <: Entity](attr: Seq[Attribute])(implicit format: Format[E]) = format.reads(attr)

  def tableFor(tableName: Symbol) = dynamoDB.table(tableName.name).get
}
