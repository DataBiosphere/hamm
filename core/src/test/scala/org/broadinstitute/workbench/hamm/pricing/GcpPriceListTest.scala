package org.broadinstitute.workbench.ccm.pricing
import java.text.SimpleDateFormat
import java.time.Instant

import minitest.SimpleTestSuite
import io.circe.parser._
import JsonCodec._
import org.broadinstitute.workbench.ccm._


object GcpPricingTest extends CcmTestSuite {
//  test("SKUsDecoder should be able to decode SKUs"){
//    val gcpPricing = new GcpPricing()
//    val customPriceList = gcpPricing.getCustomPriceList(cromwellMetaData.calls.head.region, sampleSkus)
//      val expectedResponse = PriceList(1, 1, 1, 1, 1, 1, 1, 1)
//    assertEquals(customPriceList, expectedResponse)
//  }



  val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  def toInstant(time: String): Instant = formatter.parse(time).toInstant

  val cromwellMetaData = MetadataResponse(
    List(Call(
      RuntimeAttributes(CpuNumber(1), Disks(DiskName("local-disk"), DiskSize(1), DiskType("HDD")), BootDiskSizeGb(10), Preemptible(3)),
      List(ExecutionEvent(ExecutionEventDescription("delocalizing-files"),    toInstant("2019-01-02T22:14:05.438689657Z"), toInstant("2019-01-02T22:14:09.779343193Z")),
        ExecutionEvent(ExecutionEventDescription("UpdatingJobStore"),         toInstant("2019-01-02T22:14:39.825Z"),       toInstant("2019-01-02T22:14:40.799Z")),
        ExecutionEvent(ExecutionEventDescription("ok"),                       toInstant("2019-01-02T22:14:09.779343193Z"), toInstant("2019-01-02T22:14:10Z")),
        ExecutionEvent(ExecutionEventDescription("waiting for quota"),        toInstant("2019-01-02T22:11:04Z"),           toInstant("2019-01-02T22:11:27Z")),
        ExecutionEvent(ExecutionEventDescription("RequestingExecutionToken"), toInstant("2019-01-02T22:10:13.687Z"),       toInstant("2019-01-02T22:10:13.979Z")),
        ExecutionEvent(ExecutionEventDescription("RunningJob"),               toInstant("2019-01-02T22:11:02.884Z"),       toInstant("2019-01-02T22:11:04Z")),
        ExecutionEvent(ExecutionEventDescription("UpdatingCallCache"),        toInstant("2019-01-02T22:14:39.160Z"),       toInstant("2019-01-02T22:14:39.825Z")),
        ExecutionEvent(ExecutionEventDescription("pulling-image"),            toInstant("2019-01-02T22:12:47.780575142Z"), toInstant("2019-01-02T22:12:52.779343466Z")),
        ExecutionEvent(ExecutionEventDescription("cromwell poll interval"),   toInstant("2019-01-02T22:14:10Z"),           toInstant("2019-01-02T22:14:39.160Z")),
        ExecutionEvent(ExecutionEventDescription("localizing-files"),         toInstant("2019-01-02T22:12:52.779343466Z"), toInstant("2019-01-02T22:14:04.589980901Z")),
        ExecutionEvent(ExecutionEventDescription("Pending"),                  toInstant("2019-01-02T22:10:13.686Z"),       toInstant("2019-01-02T22:10:13.687Z")),
        ExecutionEvent(ExecutionEventDescription("start"),                    toInstant("2019-01-02T22:12:46.103634373Z"), toInstant("2019-01-02T22:12:47.780575142Z")),
        ExecutionEvent(ExecutionEventDescription("WaitingForValueStore"),     toInstant("2019-01-02T22:10:13.979Z"),       toInstant("2019-01-02T22:10:13.979Z")),
        ExecutionEvent(ExecutionEventDescription("initializing VM"),          toInstant("2019-01-02T22:11:27Z"),           toInstant("2019-01-02T22:12:46.103634373Z")),
        ExecutionEvent(ExecutionEventDescription("running-docker"),           toInstant("2019-01-02T22:14:04.589980901Z"), toInstant("2019-01-02T22:14:05.438689657Z")),
        ExecutionEvent(ExecutionEventDescription("CheckingCallCache"),        toInstant("2019-01-02T22:11:02.874Z"),       toInstant("2019-01-02T22:11:02.884Z")),
        ExecutionEvent(ExecutionEventDescription("PreparingJob"),             toInstant("2019-01-02T22:10:13.979Z"),       toInstant("2019-01-02T22:11:02.874Z"))),
      false,
      true,
      Region("us-central1-c"),
      Status("Success"),
      MachineType("us-central1-c/f1-micro"),
      BackEnd("JES"),
      Attempt(1))),
    toInstant("2019-01-02T22:10:07.088Z"),
    toInstant("2019-01-02T22:14:47.266Z")
  )

  val sampleSkus =  GooglePriceList(
    List(GooglePriceItem(
      SkuName("services/6F81-5844-456A/skus/077F-E880-3C8E"),
      SkuId("077F-E880-3C8E"),
      SkuDescription("Preemptible Custom Extended Instance Core running in Sydney"),
      Category(ServiceDisplayName("Compute Engine"),
        ResourceFamily("Compute"),
        ResourceGroup("CPU"),
        UsageType("Preemptible")),
      List(ServiceRegion("australia-southeast1")),
      List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(204000000))))))))
}

