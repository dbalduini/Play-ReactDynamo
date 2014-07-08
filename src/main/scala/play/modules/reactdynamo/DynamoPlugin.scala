package play.modules.reactdynamo

import akka.actor.ActorSystem
import awscala.dynamodbv2.{DynamoDB, Item}
import com.amazonaws.services.dynamodbv2.model.Condition
import play.api._

import scala.concurrent.Future

class DynamoPlugin(implicit app: Application) extends Plugin {

  import play.libs.Akka

  type DB = String => DynamoAsync

  val db: DB = new DynamoAsync(_, Akka.system)

  override def enabled: Boolean = super.enabled

  override def onStart(): Unit = {
    Logger.info("DynamoPlugin successfully started")
  }

  override def onStop(): Unit = {
    Logger.info("DynamoPlugin stoped")
  }
}

object DynamoPlugin {

  def db = current.db

  def current: DynamoPlugin = Play.current.plugin[DynamoPlugin] match {
    case Some(plugin) => plugin
    case _ => throw new PlayException("DynamoPlugin Error", "The DynamoPlugin has not been initialized! Please edit your conf/play.plugins file and add the following line: '1100:plugins.DynamoPlugin' (1100 is an arbitrary priority and may be changed to match your needs).")
  }

}

class DynamoAsync(tableName: String, system: ActorSystem) {

  import system.dispatcher

  val table = DynamoHelper.tableFor(Symbol(tableName))

  implicit val dynamoDB = DynamoDB.local()
  type ScanParams = Seq[(String, Condition)]

  def scan[E <: Entity](params: ScanParams)(implicit format: Format[E]): Future[Seq[E]] = Future {
    val items: Seq[Item] = table scan params
    items.map { item => DynamoHelper.fromTable(item.attributes)}
  }

  def put[E <: Entity](entity: E)(implicit format: Format[E]): Future[Unit] = Future {
    table.rangePK match {
      case Some(range) =>
        val params = DynamoHelper.toTable[E](entity)
        table.put(entity.hashPK, entity.rangePK, params: _*)
      case None => table.put(entity.hashPK, DynamoHelper.toTable[E](entity): _*)
    }
  }
}