package org.broadinstitute.dsp.workbench.hamm.db

import java.time.Instant

import io.circe._
import io.circe.parser._
import org.broadinstitute.dsp.workbench.hamm.model._
import org.postgresql.util.PGobject
import scalikejdbc._
import scalikejdbc.DBSession

trait PricingCalculatorPriceTableQueries {
  def insertPriceQuery(price: PricingCalculatorPriceRecord)(implicit session: DBSession): Int
  def getPriceRecordQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey)(implicit session: DBSession): Option[PricingCalculatorPriceRecord]
  def getPriceQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double]
}

class PricingCalculatorPriceTable extends PricingCalculatorPriceTableQueries {

  val p = PricingCalculatorPriceRecord.syntax("p")

  override def insertPriceQuery(price: PricingCalculatorPriceRecord)(implicit session: DBSession): Int = {
    import PricingCalculatorPriceBinders._
    val column = PricingCalculatorPriceRecord.column
    withSQL {
      insert.into(PricingCalculatorPriceRecord).namedValues(
        column.name -> price.name,
        column.effectiveDate -> price.effectiveDate,
        column.priceItem -> price.priceItem)
    }.update.apply()
  }

  override def getPriceRecordQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey)(implicit session: DBSession): Option[PricingCalculatorPriceRecord] = {
    withSQL {
      select.from(PricingCalculatorPriceRecord as p)
        .where.eq(p.name, priceUniqueKey.name)
        .and.eq(p.effectiveDate, priceUniqueKey.effectiveDate)
    }.map(PricingCalculatorPriceRecord(p.resultName)).single().apply()
  }

  def getPriceQuery(priceUniqueKey: PricingCalculatorPriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double] = {

    val priceItemSelect = "(PRICE_ITEM->'us-central1')"

//    val sqlString = s"""SELECT (PRICE_ITEM->'us-central1') FROM PRICING_CALCULATOR_PRICE pr
//           WHERE pr.NAME = '${priceUniqueKey.name}'
//           AND   pr.EFFECTIVE_DATE < '${priceUniqueKey.effectiveDate}'
//           ORDER BY pr.EFFECTIVE_DATE DESC;"""
//
//    val unsafeString = SQLSyntax.createUnsafely(sqlString)
//
//    val thing = sql"$unsafeString"
//    println("STATEMENTT" + thing.statement)
//    val sqlDSL = withSQL {
//      select(sqls"""PRICE_ITEM->'us-central1'""").from(PricingCalculatorPriceRecord as p)
//        .where.eq(p.name, priceUniqueKey.name)
//        .and.le(p.effectiveDate, priceUniqueKey.effectiveDate)
//        .orderBy(p.effectiveDate).desc }
//
////    println("SQLSTRING STATEMENT " + sqlString.statement)
////    println("SQLSTRING PARAMETERS" + sqlString.parameters)
//      sqlDSL.map(rs => {
//        println("RESULT SET " + rs.underlying)
//        rs.double(1)
//      }).single().apply()
//   /// print("RETURN THING: " + returnThing)
//    //.map(rs => WorkflowCollectionId(rs.string(w.resultName.workflowCollectionId))).single().apply()
     Some(3)
  }

}

final case class PricingCalculatorPriceUniqueKey(name: String,
                                                 effectiveDate: Instant)

final case class PricingCalculatorPriceRecord(name: String,
                                              effectiveDate: Instant,
                                              priceItem: Json){
  val uniqueKey = PricingCalculatorPriceUniqueKey(name, effectiveDate)
}

object PricingCalculatorPriceRecord extends SQLSyntaxSupport[PricingCalculatorPriceRecord] {
  override def tableName: String = "PRICING_CALCULATOR_PRICE"
  import PricingCalculatorPriceBinders._
  def apply(e: ResultName[PricingCalculatorPriceRecord])(rs: WrappedResultSet): PricingCalculatorPriceRecord = PricingCalculatorPriceRecord(
    rs.get(e.name),
    rs.get(e.effectiveDate),
    rs.get(e.priceItem)
  )
}


object PricingCalculatorPriceBinders {

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