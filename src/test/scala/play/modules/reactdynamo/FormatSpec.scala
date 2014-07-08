package play.modules.reactdynamo

import org.specs2.mutable._
import awscala.dynamodbv2.Attribute
import awscala.dynamodbv2.AttributeValue

class FormatSpec extends Specification {

  case class Test(userId: String, name: String, num: Int) extends Entity {
    def tableName: String = "Test"
    def hashPK: Any = userId
    def rangePK: Option[Any] = Some(name)
  }

  implicit def clubFormat = Dynamo.format[Test]

  "FormatSpec" should {

    "Write a Club entity to a table format" in {
      val club = new Test("1234", "nome", 2)
      val table = DynamoHelper.toTable(club)
      println(table)
      table must not beEmpty
    }

    "Reads an attribute sequence and parse to an Entity" in {
      val attr = Seq(
        Attribute("userId", new AttributeValue()),
        Attribute("name", new AttributeValue()),
        Attribute("num", new AttributeValue()))
      val entity = DynamoHelper.fromTable[Test](attr)
      println(entity)
      entity must_== Test("", "", 0)
    }

  }

}
