package org.broadinstitute.dsp.workbench.hamm

import java.time.Instant

import org.broadinstitute.dsp.workbench.hamm.db.{CallName, Job, JobUniqueKey, Workflow}
import org.broadinstitute.dsp.workbench.hamm.model.{SamResource, SamResourceAction, WorkflowCollectionId, WorkflowId}
import org.http4s.AuthScheme
import org.http4s.Credentials.Token


object TestData {

  val testToken = Token(AuthScheme.Bearer, "fake-id")
  val testSamResource = SamResource("fake-wf-collection-id")
  val testSamResourceAction = SamResourceAction("get_cost")

  val testWorkflowId = WorkflowId("fake-id")
  val testWorkflowCollectionId = WorkflowCollectionId("fake-wf-collection-id")
  val testWorkflow = Workflow(testWorkflowId, None, None, testWorkflowCollectionId, false, Instant.now(), Instant.now(), Map.empty[String,String], 1)


  val testCallName = CallName("fake-call-name")
  val testAttempt = 2.toShort
  val testJobIndex = 3
  val testJobUniqueKey = JobUniqueKey(testWorkflowId, testCallName, 2, 3)
  val testJob = Job(testWorkflowId, testCallName, testAttempt, testJobIndex, Some("fake-vendor-id"), Instant.now(), Instant.now(), 1)




  val sampleGooglePriceJson: String =
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
