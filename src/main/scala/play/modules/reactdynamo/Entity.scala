package play.modules.reactdynamo

trait Entity {
  def tableName: String

  def hashPK: Any

  def rangePK: Option[Any]

  lazy val table = DynamoHelper.tableFor(Symbol(tableName))
}
