package org.broadinstitute.dsp.workbench.hamm.db

import java.sql.ResultSet
import java.time.Instant

import io.circe._
import io.circe.parser._
import org.broadinstitute.dsp.workbench.hamm.model.{HammException, PriceType}
import org.postgresql.util.PGobject
import scalikejdbc._
import scalikejdbc.DBSession

trait PriceTableQueries {
  def insertPriceQuery(price: PriceRecord)(implicit session: DBSession): Int
  def getPriceQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord]
}

class PriceTable extends PriceTableQueries {

  val p = PriceRecord.syntax("p")

  override def insertPriceQuery(price: PriceRecord)(implicit session: DBSession): Int = {
    import PriceBinders._
    val column = PriceRecord.column
    withSQL {
      insert.into(PriceRecord).namedValues(
        column.name -> price.name,
        column.startTime -> price.startTime,
        column.endTime -> price.endTime,
        column.priceType -> price.priceType,
        column.priceItem -> price.priceItem)
    }.update.apply()
  }

  override def getPriceQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord] = {
    withSQL {
      select.from(PriceRecord as p)
        .where.eq(p.name, priceUniqueKey.name)
        .and.eq(p.startTime, priceUniqueKey.startTime)
        .and.eq(p.endTime, priceUniqueKey.endTime)
    }.map(PriceRecord(p.resultName)).single().apply()
  }

}

final case class PriceUniqueKey(name: String,
                              startTime: Instant,
                              endTime: Instant)

final case class PriceRecord(name: String,
                             startTime: Instant,
                             endTime: Instant,
                             priceType: PriceType,
                             priceItem: Json){
  val uniqueKey = PriceUniqueKey(name, startTime, endTime)
}

object PriceRecord extends SQLSyntaxSupport[PriceRecord] {
  override def tableName: String = "PRICE"
  import PriceBinders._
  def apply(e: ResultName[PriceRecord])(rs: WrappedResultSet): PriceRecord = PriceRecord(
    rs.get(e.name),
    rs.get(e.startTime),
    rs.get(e.endTime),
    rs.get(e.priceType),
    rs.get(e.priceItem)
  )
}


object PriceBinders {

  implicit val priceTypeBinder: TypeBinder[PriceType] = new TypeBinder[PriceType] {
    def apply(rs: ResultSet, label: String): PriceType = PriceType.stringToPriceType(rs.getString(label))
    def apply(rs: ResultSet, index: Int): PriceType = PriceType.stringToPriceType(rs.getString(index))
  }


  implicit val priceTypePbf: ParameterBinderFactory[PriceType] = ParameterBinderFactory[PriceType] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val PriceItemTypeBinder: TypeBinder[Json] = {
    TypeBinder.option[String].map { strOption =>
      strOption.map{ str => parse(str) match {
        case Left(parsingFailure: ParsingFailure) => throw parsingFailure
        case Right(json: Json) => json
      }
      }.getOrElse(throw HammException(404, "Price Item String not found."))
    }
  }

  implicit val priceItemPbf: ParameterBinderFactory[Json] = ParameterBinderFactory[Json] {
    value => (stmt, idx) => {
      val jsonObject = new PGobject()
      jsonObject.setType("jsonb")
      jsonObject.setValue(value.noSpaces)
      stmt.setObject(5, jsonObject)
    }
  }

}