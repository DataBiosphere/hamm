package org.broadinstitute.dsp.workbench.hamm.db

import java.sql.ResultSet
import java.time.Instant

import org.broadinstitute.dsp.workbench.hamm.model._
import scalikejdbc._
import scalikejdbc.DBSession

trait SKUPriceTableQueries {
  def insertPriceQuery(price: SKUPriceRecord)(implicit session: DBSession): Int
  def insertPriceQuery(priceList: GooglePriceList)(implicit session: DBSession): List[Int]
  def getPriceRecordQuery(priceUniqueKey: SKUPriceUniqueKey)(implicit session: DBSession): Option[SKUPriceRecord]
  def getPriceRecordQuery(region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, effectiveTime: Instant)(implicit session: DBSession): Option[Double]

  //def getPriceQuery(priceUniqueKey: SKUPriceUniqueKey, region: Region)(implicit session: DBSession): Option[Double]
}

class SKUPriceTable extends SKUPriceTableQueries {

  private val p = SKUPriceRecord.syntax("p")

  override def insertPriceQuery(price: SKUPriceRecord)(implicit session: DBSession): Int = {
    import SKUPriceBinders._
    val column = SKUPriceRecord.column
    withSQL {
      insert.into(SKUPriceRecord).namedValues(
        column.skuName -> price.skuName,
        column.description -> price.description,
        column.resourceFamily -> price.resourceFamily,
        column.resourceGroup -> price.resourceGroup,
        column.usageType -> price.usageType,
        column.machineType -> price.machineType,
        column.extended -> column.extended,
        column.regions -> price.regions,
        column.effectiveTime -> price.effectiveTime,
        column.usageUnit -> price.usageUnit,
        column.baseUnit -> price.baseUnit,
        column.startUsageAmount -> price.startUsageAmount,
        column.units -> price.units,
        column.currencyCode -> price.currencyCode,
        column.cost -> price.cost
      )
    }.update.apply()
  }

  override def insertPriceQuery(priceList: GooglePriceList)(implicit session: DBSession): List[Int] = {
    demarshalToPriceRecord(priceList).map { priceRecord =>
      insertPriceQuery(priceRecord)
    }
  }

  private def demarshalToPriceRecord(priceList: GooglePriceList): List[SKUPriceRecord] = {
    priceList.priceItems.flatMap { item =>
      item.pricingInfo.flatMap { pricingItem =>
        pricingItem.tieredRates.map { tieredRate =>
          SKUPriceRecord(
            item.name,
            item.description,
            item.category.resourceFamily,
            item.category.resourceGroup,
            item.category.usageType,
            if (item.description.asString.contains(MachineType.Custom.asDescriptionString)) MachineType.Custom else MachineType.SKUStringToMachineType(item.category.resourceGroup.asString),
            item.description.asString.contains("extended"),
            item.regions,
            pricingItem.effectiveTime,
            pricingItem.usageUnit,
            pricingItem.baseUnit,
            tieredRate.startUsageAmount,
            tieredRate.units,
            tieredRate.currencyCode,
            tieredRate.nanos
          )
        }
      }
    }
  }

  override def getPriceRecordQuery(priceUniqueKey: SKUPriceUniqueKey)(implicit session: DBSession): Option[SKUPriceRecord] = {
    import SKUPriceBinders._
    withSQL {
      select.from(SKUPriceRecord as p)
        .where.eq(p.skuName, priceUniqueKey.skuName)
        .and.eq(p.effectiveTime, priceUniqueKey.effectiveTime)
    }.map(SKUPriceRecord(p.resultName)).single().apply()
  }

  override def getPriceRecordQuery(region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, effectiveTime: Instant)(implicit session: DBSession): Option[Double] = {
    withSQL {
      select(p.result.cost).from(SKUPriceRecord as p)
        .where.eq(p.resourceFamily, resourceFamily.asString)
        .and.eq(p.resourceGroup, resourceGroup.asString)
        .and.eq(p.usageType, usageType.asString)
        .and(Some(sqls"${p.regions} @> ARRAY['${region.asString}']::varchar[]"))
        .and.lt(p.effectiveTime, effectiveTime)
        .orderBy(p.effectiveTime).asc
    }.map(rs => rs.double(p.resultName.cost)).single().apply()
  }

}

final case class SKUPriceUniqueKey(skuName: SkuName,
                                   effectiveTime: Instant)

final case class SKUPriceRecord(skuName: SkuName,
                                description: SkuDescription,
                                resourceFamily: ResourceFamily,
                                resourceGroup: ResourceGroup,
                                usageType: UsageType,
                                machineType: MachineType,
                                extended: Boolean,
                                regions: List[Region],
                                effectiveTime: Instant,
                                usageUnit: UsageUnit,
                                baseUnit: BaseUnit,
                                startUsageAmount: StartUsageAmount,
                                units: Units,
                                currencyCode: CurrencyCode,
                                cost: Nanos) {
  val uniqueKey = SKUPriceUniqueKey(skuName, effectiveTime)
}

object SKUPriceRecord extends SQLSyntaxSupport[SKUPriceRecord] {
  override def tableName: String = "SKU_PRICE"
  import SKUPriceBinders._
  def apply(e: ResultName[SKUPriceRecord])(rs: WrappedResultSet): SKUPriceRecord = SKUPriceRecord(
    rs.get(e.skuName),
    rs.get(e.description),
    rs.get(e.resourceFamily),
    rs.get(e.resourceGroup),
    rs.get(e.usageType),
    rs.get(e.machineType),
    rs.get(e.extended),
    rs.get(e.regions),
    rs.get(e.effectiveTime),
    rs.get(e.usageUnit),
    rs.get(e.baseUnit),
    rs.get(e.startUsageAmount),
    rs.get(e.units),
    rs.get(e.currencyCode),
    rs.get(e.cost)
  )
}


object SKUPriceBinders {
  implicit val skuNameTypeBinder: TypeBinder[SkuName] = new TypeBinder[SkuName] {
    def apply(rs: ResultSet, label: String): SkuName = SkuName(rs.getString(label))
    def apply(rs: ResultSet, index: Int): SkuName = SkuName(rs.getString(index))
  }

  implicit val skuNamePbf: ParameterBinderFactory[SkuName] = ParameterBinderFactory[SkuName] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val skuDescriptionTypeBinder: TypeBinder[SkuDescription] = new TypeBinder[SkuDescription] {
    def apply(rs: ResultSet, label: String): SkuDescription = SkuDescription(rs.getString(label))
    def apply(rs: ResultSet, index: Int): SkuDescription = SkuDescription(rs.getString(index))
  }

  implicit val skuDescriptionPbf: ParameterBinderFactory[SkuDescription] = ParameterBinderFactory[SkuDescription] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val machineTypeTypeBinder: TypeBinder[MachineType] = new TypeBinder[MachineType] {
    def apply(rs: ResultSet, label: String): MachineType = MachineType.stringToMachineType(rs.getString(label))
    def apply(rs: ResultSet, index: Int): MachineType = MachineType.stringToMachineType(rs.getString(index))
  }

  implicit val machineTypePbf: ParameterBinderFactory[MachineType] = ParameterBinderFactory[MachineType] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val UnitsTypeBinder: TypeBinder[Units] = new TypeBinder[Units] {
    def apply(rs: ResultSet, label: String): Units = Units(rs.getInt(label))
    def apply(rs: ResultSet, index: Int): Units = Units(rs.getInt(index))
  }

  implicit val UnitsPbf: ParameterBinderFactory[Units] = ParameterBinderFactory[Units] {
    value => (stmt, idx) => stmt.setInt(idx, value.asInt)
  }

  implicit val currencyCodeTypeBinder: TypeBinder[CurrencyCode] = new TypeBinder[CurrencyCode] {
    def apply(rs: ResultSet, label: String): CurrencyCode = CurrencyCode(rs.getString(label))
    def apply(rs: ResultSet, index: Int): CurrencyCode = CurrencyCode(rs.getString(index))
  }

  implicit val currencyCodePbf: ParameterBinderFactory[CurrencyCode] = ParameterBinderFactory[CurrencyCode] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val resourceFamilyTypeBinder: TypeBinder[ResourceFamily] = new TypeBinder[ResourceFamily] {
    def apply(rs: ResultSet, label: String): ResourceFamily = ResourceFamily.stringToResourceFamily(rs.getString(label))
    def apply(rs: ResultSet, index: Int): ResourceFamily = ResourceFamily.stringToResourceFamily(rs.getString(index))
  }

  implicit val resourceFamilyPbf: ParameterBinderFactory[ResourceFamily] = ParameterBinderFactory[ResourceFamily] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val resourceGroupTypeBinder: TypeBinder[ResourceGroup] = new TypeBinder[ResourceGroup] {
    def apply(rs: ResultSet, label: String): ResourceGroup = ResourceGroup(rs.getString(label))
    def apply(rs: ResultSet, index: Int): ResourceGroup = ResourceGroup(rs.getString(index))
  }

  implicit val resourceGroupPbf: ParameterBinderFactory[ResourceGroup] = ParameterBinderFactory[ResourceGroup] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val usageTypeTypeBinder: TypeBinder[UsageType] = new TypeBinder[UsageType] {
    def apply(rs: ResultSet, label: String): UsageType = UsageType.stringToUsageType(rs.getString(label))
    def apply(rs: ResultSet, index: Int): UsageType = UsageType.stringToUsageType(rs.getString(index))
  }

  implicit val usageTypePbf: ParameterBinderFactory[UsageType] = ParameterBinderFactory[UsageType] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val regionsTypeBinder: TypeBinder[List[Region]] = new TypeBinder[List[Region]] {
    def apply(rs: ResultSet, label: String): List[Region] =  rs.getArray(label).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[Region]).toList
    def apply(rs: ResultSet, index: Int): List[Region] = rs.getArray(index).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[Region]).toList
  }

  implicit val regionsPbf: ParameterBinderFactory[List[Region]] = ParameterBinderFactory[List[Region]] {
    value => (stmt, idx) => stmt.setArray(idx, stmt.getConnection.createArrayOf("VARCHAR(255)", value.map(_.asInstanceOf[AnyRef]).toArray))
  }

  implicit val usageUnitTypeBinder: TypeBinder[UsageUnit] = new TypeBinder[UsageUnit] {
    def apply(rs: ResultSet, label: String): UsageUnit = UsageUnit(rs.getString(label))
    def apply(rs: ResultSet, index: Int): UsageUnit = UsageUnit(rs.getString(index))
  }

  implicit val usageUnitPbf: ParameterBinderFactory[UsageUnit] = ParameterBinderFactory[UsageUnit] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val baseUnitTypeBinder: TypeBinder[BaseUnit] = new TypeBinder[BaseUnit] {
    def apply(rs: ResultSet, label: String): BaseUnit = BaseUnit(rs.getString(label))
    def apply(rs: ResultSet, index: Int): BaseUnit = BaseUnit(rs.getString(index))
  }

  implicit val baseUnitPbf: ParameterBinderFactory[BaseUnit] = ParameterBinderFactory[BaseUnit] {
    value => (stmt, idx) => stmt.setString(idx, value.asString)
  }

  implicit val StartUsageAmountTypeBinder: TypeBinder[StartUsageAmount] = new TypeBinder[StartUsageAmount] {
    def apply(rs: ResultSet, label: String): StartUsageAmount = StartUsageAmount(rs.getInt(label))
    def apply(rs: ResultSet, index: Int): StartUsageAmount = StartUsageAmount(rs.getInt(index))
  }

  implicit val StartUsageAmountPbf: ParameterBinderFactory[StartUsageAmount] = ParameterBinderFactory[StartUsageAmount] {
    value => (stmt, idx) => stmt.setInt(idx, value.asInt)
  }

  implicit val nanosTypeBinder: TypeBinder[Nanos] = new TypeBinder[Nanos] {
    def apply(rs: ResultSet, label: String): Nanos = Nanos(rs.getInt(label))
    def apply(rs: ResultSet, index: Int): Nanos = Nanos(rs.getInt(index))
  }

  implicit val nanosPbf: ParameterBinderFactory[Nanos] = ParameterBinderFactory[Nanos] {
    value => (stmt, idx) => stmt.setInt(idx, value.asInt)
  }

}

