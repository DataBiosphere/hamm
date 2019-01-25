package org.broadinstitute.workbench.hamm.pricing

import minitest.SimpleTestSuite
import io.circe.parser._
import JsonCodec._
import org.broadinstitute.workbench.hamm.pricing.UsageType.{Commit1Yr, OnDemand}
import org.broadinstitute.workbench.hamm.{HammTestSuite, MachineType, Region}

object JsonCodecTest extends HammTestSuite {

  test("SKUsDecoder should be able to decode SKUs"){
    val res = for {
      json <- parse(sampleTest)
      r <- json.as[GooglePriceList]
    } yield {
      val expectedResponse = GooglePriceList(
        List(
          //region Global, resourceFamily ResourceFamily(Compute), resourceGroup ResourceGroup(RAM), Preemptible usageType, notIncluding Some(Custom Extended)
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0000-2C0D-17F3"),
            SkuId("0000-2C0D-17F3"),
            SkuDescription("Preemptible Custom Extended Instance Ram running globally"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(10931000)))))),

          //region Global, resourceFamily ResourceFamily(Compute), resourceGroup ResourceGroup(CPU), Preemptible usageType, notIncluding None
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0000-BBAF-9069"),
            SkuId("0000-BBAF-9069"),
            SkuDescription("Preemptible Custom Instance Core running globally"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("global")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(6986000)))))),

          //region Global, resourceFamily ResourceFamily(Storage), resourceGroup ResourceGroup(SSD), Preemptible usageType, notIncluding Some(Regional)
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/9420-2C0D-17F3"),
            SkuId("9420-2C0D-17F3"),
            SkuDescription("Preemptible Custom Ram running globally"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("SSD"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("global")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(20931000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/472A-2C0D-17F3"),
            SkuId("472A-2C0D-17F3"),
            SkuDescription("Preemptible Custom Extended Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(10931000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/472A-2C0D-17F3"),
            SkuId("472A-2C0D-17F3"),
            SkuDescription("Custom Extended Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(10931000)))))),

          //i think this one might be wrong
          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/5662-928E-19C3"),
            SkuId("5662-928E-19C3"),
            SkuDescription("Preemptible Custom Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(1076000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/4EAF-BBAF-9069"),
            SkuId("4EAF-BBAF-9069"),
            SkuDescription("Preemptible Custom Instance Core running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(7986000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/5662-928E-19C3"),
            SkuId("5662-928E-19C3"),
            SkuDescription("Custom Instance Ram running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("RAM"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(1076000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/2037-B859-1728"),
            SkuId("2037-B859-1728"),
            SkuDescription("Custom Instance Core running in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(37970000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/878F-E2CC-F899"),
            SkuId("878F-E2CC-F899"),
            SkuDescription("Storage PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("PDStandard"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(96000000)))))),


          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0013-863C-A2FF"),
            SkuId("0013-863C-A2FF"),
            SkuDescription("Licensing Fee for SQL Server 2016 Standard on VM with 18 VCPU"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("License"),
              ResourceGroup("SQLServer2016Standard"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("global")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(2), Nanos(961000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/001D-204A-23DA"),
            SkuId("001D-204A-23DA"),
            SkuDescription("Commitment v1: Cpu in Montreal for 1 Year"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType.stringToUsageType("Commit1Yr")),
            List(Region.stringToRegion("northamerica-northeast1")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(21925000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/0589-AA00-68BD"),
            SkuId("0589-AA00-68BD"),
            SkuDescription("SSD backed PD Capacity in Los Angeles"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Storage"),
              ResourceGroup("SSD"),
              UsageType.stringToUsageType("OnDemand")),
            List(Region.stringToRegion("us-west2")),
            List(PricingInfo(UsageUnit("GiBy.mo"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(204000000)))))),

          GooglePriceItem(
            SkuName("services/6F81-5844-456A/skus/077F-E880-3C8E"),
            SkuId("077F-E880-3C8E"),
            SkuDescription("Preemptible Custom Extended Instance Core running in Sydney"),
            Category(ServiceDisplayName("Compute Engine"),
              ResourceFamily("Compute"),
              ResourceGroup("CPU"),
              UsageType.stringToUsageType("Preemptible")),
            List(Region.stringToRegion("australia-southeast1")),
            List(PricingInfo(UsageUnit("h"), List(TieredRate(StartUsageAmount(0), CurrencyCode("USD"), Units(0), Nanos(8980000))))))))

      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }

  test("SKUsDecoder should be able to decode PriceList"){
    val region = Region.stringToRegion("us-west2")
    val machineType = MachineType.Custom
    val res = for {
      json <- parse(sampleTest)
      googlePriceList <- json.as[GooglePriceList]
      r <- GcpPricing.getPriceList(googlePriceList)
    } yield {
//      val expectedResponse = PriceList(region,
//        machineType,
//        0.0002794520547945205,
//        0.0001315068493150685,
//        0.03797,
//        0.001076,
//        0.010931,
//        0.007986,
//        0.001076,
//        0.010931)
      val expectedResponse = PriceList(Map()) //fix this test
      assertEquals(r, expectedResponse)
    }
    res.fold[Unit](e => throw e, identity)
  }


  def makeSkuJson(region: Region, resourceFamily: ResourceFamily, resourceGroup: ResourceGroup, usageType: UsageType, machineType: MachineType, extended: Boolean, price: Nanos): String = {
    def description = s"${usageType.asDescriptionString} ${machineType.asDescriptionString} ${if (extended) "extended" else ""} some more description"

    s"""{
      "name": "test-name",
      "skuId": "test-id",
      "description": "$description",
      "category": {
        "serviceDisplayName": "Compute Engine",
        "resourceFamily": "$resourceFamily",
        "resourceGroup": "$resourceGroup",
        "usageType": "$usageType"
      },
      "serviceRegions": [
      "$region"
      ],
      "pricingInfo": [
      {
        "summary": "",
        "pricingExpression": {
          "usageUnit": "test-unit",
          "usageUnitDescription": "test-unit",
          "baseUnit": "s",
          "baseUnitDescription": "second",
          "baseUnitConversionFactor": 3600,
          "displayQuantity": 1,
          "tieredRates": [
        {
          "startUsageAmount": 0,
          "unitPrice": {
          "currencyCode": "USD",
          "units": "0",
          "nanos": ${price.asInt}
        }
        }
          ]
        },
        "currencyConversionRate": 1,
        "effectiveTime": "2019-01-17T13:07:15.915Z"
      }
      ],
      "serviceProviderName": "Google"
    }"""
  }

  val sampleTest: String =
    """
      |{
      |  "skus": [
      |     {
      |      "name": "services/6F81-5844-456A/skus/0000-BBAF-9069",
      |      "skuId": "0000-BBAF-9069",
      |      "description": "Preemptible Custom Instance Core running globally",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Compute",
      |        "resourceGroup": "CPU",
      |        "usageType": "Preemptible"
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
      |                  "units": "0",
      |                  "nanos": 6986000
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
      |      "name": "services/6F81-5844-456A/skus/9420-2C0D-17F3",
      |      "skuId": "9420-2C0D-17F3",
      |      "description": "Preemptible Custom Ram running globally",
      |      "category": {
      |        "serviceDisplayName": "Compute Engine",
      |        "resourceFamily": "Storage",
      |        "resourceGroup": "SSD",
      |        "usageType": "Preemptible"
      |      },
      |      "serviceRegions": [
      |        "global"
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
      |                  "nanos": 20931000
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
