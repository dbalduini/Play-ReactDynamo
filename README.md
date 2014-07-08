# ReactDynamo - Asynchronous & Non-Blocking AWScala support to Play! Framework 2.2

This is a plugin for Play 2.2, enabling support for AWScala, but with an Asynchronous & Non-Blocking implementation.


## Add ReactDynamo to your dependencies

```scala
libraryDependencies ++= Seq(
  "io.react2" %% "play-reactdynamo" % "1.0-SNAPSHOT"
)
```

## Configure your application to use ReactDynamo plugin
### add to your conf/play.plugins
```
1100:play.modules.reactdynamo.DynamoPlugin
```

## Play2 Controller sample

```scala

import play.modules.reactdynamo._
import awscala.dynamodbv2.Condition
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Test(userId: String, name: String, num: Int) extends Entity {
  def tableName: String = "Test"
  def hashPK: Any = userId
  def rangePK: Option[Any] = Some(name)
}

object MyController extends Controller {
  
  implicit def testFormat = Dynamo.format[Test]

  val table = DynamoPlugin.db("TestTable")


  def testScan(name: String) = Action.async {
    table.scan[Test](Seq("name" -> Condition.eq(name))).map {
      _.toList match {
        case head :: tail => Ok(head.toString)
        case _ => NotFound
      }
    }
  }

}
```


