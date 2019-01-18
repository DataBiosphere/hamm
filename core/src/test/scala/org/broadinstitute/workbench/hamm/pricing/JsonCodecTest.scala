package org.broadinstitute.workbench.ccm.pricing

import minitest.SimpleTestSuite
import io.circe.parser._
import JsonCodec._
import org.broadinstitute.workbench.ccm.{CcmTestSuite, MachineType, Region}

object JsonCodecTest extends CcmTestSuite {
  test("SKUsDecoder should be able to decode SKUs"){
    val res = for {
      json <- parse(sampleTest)
      r <- json.as[GooglePriceList]
    } yield {
      val expectedResponse = GooglePriceList(
        List(
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/472A-2C0D-17F3"),
            SkuId("472A-2C0D-17F3"),
            SkuDescription("Custom Extended Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(10931000)))))),

          //i think this one might be wrong
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/5662-928E-19C3"),
            SkuId("5662-928E-19C3"),
            SkuDescription("Custom Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(1076000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/2037-B859-1728"),
            SkuId("2037-B859-1728"),
            SkuDescription("Custom Instance Core running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(37970000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/878F-E2CC-F899"),
            SkuId("878F-E2CC-F899"),
            SkuDescription("Storage PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("PDStandard"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(96000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/14FF-BB6D-E96F"),
            SkuId("14FF-BB6D-E96F"),
            SkuDescription("SSD backed PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("SSD"),
              UsageType("OnDemand")),
            List(ServiceRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(287000000)))))),


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

  test("SKUsDecoder should be able to decode PriceList"){
    val region = Region("us-west2")
    val machineType = MachineType("custom")
    implicit val priceListDecoder = JsonCodec.PriceListDecoder(region, machineType)
    val res = for {
      json <- parse(sampleTest)
      r <- json.as[PriceList]
    } yield {
      val expectedResponse = PriceList(region,
        machineType,
        0.0003931506,
        0.0001315068,
        0.03797,
        0.001076,
        0.010931,
        0.007986,
        0.001076,
        0.010931)
      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }

  val sampleTest: String =
    """
      |{
      |  "skus": [
      |  {
      |      "name": "services/6F81-5844-456A/skus/472A-2C0D-17F3",
      |      "skuId": "472A-2C0D-17F3",
      |      "description": "Custom Extended Instance Ram running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "RAM",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "GiBy.h",
      |            "usageUnitDescription": "gibibyte hour",
      |            "baseUnit": "By.s",
      |            "baseUnitDescription": "byte second",
      |            "baseUnitConversionFactor": 3865470566400,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 10931000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |   {
      |      "name": "services/6F81-5844-456A/skus/5662-928E-19C3",
      |      "skuId": "5662-928E-19C3",
      |      "description": "Preemptible Custom Instance Ram running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "RAM",
      |        "usageType": "Preemptible"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "GiBy.h",
      |            "usageUnitDescription": "gibibyte hour",
      |            "baseUnit": "By.s",
      |            "baseUnitDescription": "byte second",
      |            "baseUnitConversionFactor": 3865470566400,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 1076000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |   {
      |      "name": "services/6F81-5844-456A/skus/4EAF-BBAF-9069",
      |      "skuId": "4EAF-BBAF-9069",
      |      "description": "Preemptible Custom Instance Core running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "CPU",
      |        "usageType": "Preemptible"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
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
      |                  "nanos": 7986000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |  {
      |      "name": "services/6F81-5844-456A/skus/472A-2C0D-17F3",
      |      "skuId": "472A-2C0D-17F3",
      |      "description": "Custom Extended Instance Ram running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "RAM",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "GiBy.h",
      |            "usageUnitDescription": "gibibyte hour",
      |            "baseUnit": "By.s",
      |            "baseUnitDescription": "byte second",
      |            "baseUnitConversionFactor": 3865470566400,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 10931000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |      {
      |      "name": "services/6F81-5844-456A/skus/5662-928E-19C3",
      |      "skuId": "5662-928E-19C3",
      |      "description": "Custom Instance Ram running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "RAM",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
      |      ],
      |      "pricingInfo": [
      |        {
      |          "summary": "",
      |          "pricingExpression": {
      |            "usageUnit": "GiBy.h",
      |            "usageUnitDescription": "gibibyte hour",
      |            "baseUnit": "By.s",
      |            "baseUnitDescription": "byte second",
      |            "baseUnitConversionFactor": 3865470566400,
      |            "displayQuantity": 1,
      |            "tieredRates": [
      |              {
      |                "startUsageAmount": 0,
      |                "unitPrice": {
      |                  "currencyCode": "USD",
      |                  "units": "0",
      |                  "nanos": 1076000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |  {
      |      "name": "services/6F81-5844-456A/skus/2037-B859-1728",
      |      "skuId": "2037-B859-1728",
      |      "description": "Custom Instance Core running in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "CPU",
      |        "usageType": "OnDemand"
      |      },
      |      "serviceRegions": [
      |        "us-west2"
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
      |                  "nanos": 37970000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |  {
      |      "name": "services/6F81-5844-456A/skus/878F-E2CC-F899",
      |      "skuId": "878F-E2CC-F899",
      |      "description": "Storage PD Capacity in Los Angeles",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Storage",
      |        "resourceGroup": "PDStandard",
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
      |                  "nanos": 96000000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
      |    {
      |      "name": "services/6F81-5844-456A/skus/14FF-BB6D-E96F",
      |      "skuId": "14FF-BB6D-E96F",
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
      |                  "nanos": 287000000
      |                }
      |              }
      |            ]
      |          },
      |          "currencyConversionRate": 1,
      |          "effectiveTime": "2019-01-17T13:07:15.915Z"
      |        }
      |      ],
      |      "serviceProviderName": "Google"
      |    },
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
