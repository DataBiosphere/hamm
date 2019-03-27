package org.broadinstitute.dsp.workbench.hamm.db

import java.time.Instant

import io.circe._
import io.circe.parser._
import org.broadinstitute.dsp.workbench.hamm.model._
import org.postgresql.util.PGobject
import scalikejdbc._
import scalikejdbc.DBSession

trait PriceTableQueries {
  def insertPriceQuery(price: PriceRecord)(implicit session: DBSession): Int
  def getPriceRecordQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord]
  def getPriceQuery(priceUniqueKey: PriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double]
}

class PriceTable extends PriceTableQueries {

  val p = PriceRecord.syntax("p")

  override def insertPriceQuery(price: PriceRecord)(implicit session: DBSession): Int = {
    import PriceBinders._
    val column = PriceRecord.column
    withSQL {
      insert.into(PriceRecord).namedValues(
        column.name -> price.name,
        column.effectiveDate -> price.effectiveDate,
        column.priceItem -> price.priceItem)
    }.update.apply()
  }

  override def getPriceRecordQuery(priceUniqueKey: PriceUniqueKey)(implicit session: DBSession): Option[PriceRecord] = {
    withSQL {
      select.from(PriceRecord as p)
        .where.eq(p.name, priceUniqueKey.name)
        .and.eq(p.effectiveDate, priceUniqueKey.effectiveDate)
    }.map(PriceRecord(p.resultName)).single().apply()
  }

  def getPriceQuery(priceUniqueKey: PriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double] = {
    sql"""SELECT PRICE_ITEM#>'${region.asString}' FROM PRICE pr
           WHERE pr.NAME = ${priceUniqueKey.name}
           AND   pr.EFFECTIVE_DATE < ${priceUniqueKey.effectiveDate}
           ORDER BY pr.EFFECTIVE_DATE DESC"""
      .map(rs => rs.double(1)).single().apply()
     // .map(PriceRecord(p.resultName)).single().apply()
  }
}

final case class PriceUniqueKey(name: String,
                                effectiveDate: Instant)

final case class PriceRecord(name: String,
                             effectiveDate: Instant,
                             priceItem: Json){
  val uniqueKey = PriceUniqueKey(name, effectiveDate)
}

object PriceRecord extends SQLSyntaxSupport[PriceRecord] {
  override def tableName: String = "PRICE"
  import PriceBinders._
  def apply(e: ResultName[PriceRecord])(rs: WrappedResultSet): PriceRecord = PriceRecord(
    rs.get(e.name),
    rs.get(e.effectiveDate),
    rs.get(e.priceItem)
  )
}


object PriceBinders {

//  implicit val priceTypeBinder: TypeBinder[PriceType] = new TypeBinder[PriceType] {
//    def apply(rs: ResultSet, label: String): PriceType = PriceType.stringToPriceType(rs.getString(label))
//    def apply(rs: ResultSet, index: Int): PriceType = PriceType.stringToPriceType(rs.getString(index))
//  }
//
//
//  implicit val priceTypePbf: ParameterBinderFactory[PriceType] = ParameterBinderFactory[PriceType] {
//    value => (stmt, idx) => stmt.setString(idx, value.asString)
//  }

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
      stmt.setObject(4, jsonObject)
    }
  }

}