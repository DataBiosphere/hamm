package org.broadinstitute.workbench.ccm.pricing

import minitest.SimpleTestSuite
import io.circe.parser._
import JsonCodec._
import org.broadinstitute.workbench.ccm.CcmTestSuite

object JsonCodecTest extends CcmTestSuite {
  test("SKUsDecoder should be able to decode SKUs"){
    val res = for {
      json <- parse(sampleTest)
      r <- json.as[GooglePriceList]
    } yield {
      val expectedResponse = GooglePriceList(
        List(
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0013-863C-A2FF"),
            SkuId("0013-863C-A2FF"),
            SkuDescription("Licensing Fee for SQL Server 2016 Standard on VM with 18 VCPU"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("License"),
              ResourceGroup("SQLServer2016Standard"),
              UsageType("OnDemand")),
            List(ServiceRegion("global")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(2), Nanos(961000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/001D-204A-23DA"),
            SkuId("001D-204A-23DA"),
            SkuDescription("Commitment v1: Cpu in Montreal for 1 Year"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType("Commit1Yr")),
            List(ServiceRegion("northamerica-northeast1")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(21925000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0589-AA00-68BD"),
            SkuId("0589-AA00-68BD"),
            SkuDescription("SSD backed PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("SSD"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(204000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/077F-E880-3C8E"),
            SkuId("077F-E880-3C8E"),
            SkuDescription("Preemptible Custom Extended Instance Core running in Sydney"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType("Preemptible")),
            List(ServiceRegion("australia-southeast1")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(8980000))))))))

      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }

  val sampleTest: String =
    """
      |{
      |  "skus": [
      |    {
      |      "name": "services/6F81-5844-456A/skus/0013-863C-A2FF",
      |      "skuId": "0013-863C-A2FF",
      |      "description": "Licensing Fee for SQL Server 2016 Standard on VM with 18 VCPU",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "License",
      |        "resourceGroup": "SQLServer2016Standard",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "global"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "h",
      |            "usageUnitDescription": "hour",
      |            "baseUnit": "s",
      |            "baseUnitDescription": "second",
      |            "baseUnitConversionFactor": 3600,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "2",
      |                  "nanos": 961000000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-04T01:08:22.878Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |    {
      |      "name": "services/6F81-5844-456A/skus/001D-204A-23DA",
      |      "skuId": "001D-204A-23DA",
      |      "description": "Commitment v1: Cpu in Montreal for 1 Year",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "CPU",
      |        "usageType": "Commit1Yr"
      |      },
      |      "serviceRegions": [
      |        "northamerica-northeast1"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "h",
      |            "usageUnitDescription": "hour",
      |            "baseUnit": "s",
      |            "baseUnitDescription": "second",
      |            "baseUnitConversionFactor": 3600,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 21925000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-04T01:08:22.878Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |    {
      |      "name": "services/6F81-5844-456A/skus/0589-AA00-68BD",
      |      "skuId": "0589-AA00-68BD",
      |      "description": "SSD backed PD Capacity in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Storage",
      |        "resourceGroup": "SSD",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "GiBy.mo",
      |            "usageUnitDescription": "gibibyte month",
      |            "baseUnit": "By.s",
      |            "baseUnitDescription": "byte second",
      |            "baseUnitConversionFactor": 2.8759101014016e+15,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 204000000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-04T01:08:22.878Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |    {
      |      "name": "services/6F81-5844-456A/skus/077F-E880-3C8E",
      |      "skuId": "077F-E880-3C8E",
      |      "description": "Preemptible Custom Extended Instance Core running in Sydney",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "CPU",
      |        "usageType": "Preemptible"
      |      },
      |      "serviceRegions": [
      |        "australia-southeast1"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "h",
      |            "usageUnitDescription": "hour",
      |            "baseUnit": "s",
      |            "baseUnitDescription": "second",
      |            "baseUnitConversionFactor": 3600,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 8980000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-04T01:08:22.878Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    }
      |    ]
      | }
    """.stripMargin
}
