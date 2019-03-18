package org.broadinstitute.dsp.workbench.hamm

import java.time.Instant
import io.circe.parser._

import org.broadinstitute.dsp.workbench.hamm.db._
import org.broadinstitute.dsp.workbench.hamm.model._
import org.http4s.AuthScheme
import org.http4s.Credentials.Token


object TestData {

  val testToken = Token(AuthScheme.Bearer, "fake-id")
  val testSamResource = SamResource("fake-wf-collection-id")
  val testSamResourceAction = SamResourceAction("get_cost")

  val testWorkflowId = WorkflowId("fake-id")
  val testWorkflowCollectionId = WorkflowCollectionId("fake-wf-collection-id")
  val testWorkflow = Workflow(testWorkflowId, None, None, testWorkflowCollectionId, false, Instant.now(), Instant.now(), Map.empty[String, String], 1)


  val testCallFqn = CallFqn("fake-call-fqn")
  val testAttempt = 2.toShort
  val testJobIndex = 3
  val testJobUniqueKey = JobUniqueKey(testWorkflowId, testCallFqn, testAttempt, testJobIndex)
  val testJob = Job(testWorkflowId, testCallFqn, testAttempt, testJobIndex, Some("fake-vendor-id"), Instant.now(), Instant.now(), 1)

  val testPriceName = "fake-price-name"
  val testStartTime = Instant.now()
  val testEndTime = Instant.now()
  val testPriceUniqueKey = PriceUniqueKey(testPriceName, testStartTime, testEndTime)
  val testPriceType = PriceType.Regional
  val testPriceItem = parse(
    """{
      |      "us": 0.0076,
      |      "us-central1": 0.0076,
      |      "us-east1": 0.0076,
      |      "us-east4": 0.0086,
      |      "us-west1": 0.0076,
      |      "us-west2": 0.0091,
      |      "europe": 0.0086,
      |      "europe-west1": 0.0086,
      |      "europe-west2": 0.0096,
      |      "europe-west3": 0.0096,
      |      "europe-west4": 0.0084,
      |      "europe-west6": 0.010600,
      |      "europe-north1": 0.0084,
      |      "northamerica-northeast1": 0.0084,
      |      "asia": 0.0090,
      |      "asia-east": 0.0090,
      |      "asia-east1": 0.0090,
      |      "asia-east2": 0.0106,
      |      "asia-northeast": 0.0092,
      |      "asia-northeast1": 0.0092,
      |      "asia-northeast2": 0.0092,
      |      "asia-southeast": 0.0092,
      |      "australia-southeast1": 0.0106,
      |      "australia": 0.0106,
      |      "southamerica-east1": 0.0118,
      |      "asia-south1": 0.0091,
      |      "cores": "shared",
      |      "memory": "0.6",
      |      "gceu": "Shared CPU, not guaranteed",
      |      "maxNumberOfPd": 16,
      |      "maxPdSize": 64,
      |      "ssd": [
      |        0
      |      ]
      |    }""".stripMargin).toOption.get
  val testPriceRecord = PriceRecord(testPriceName, testStartTime, testEndTime, testPriceType, testPriceItem)


  val sampleList =
    """{
      |  "comment": "This JSON data is obsolete. Please use https://cloud.google.com/billing/v1/how-tos/catalog-api instead.",
      |  "version": "v1.65",
      |  "updated": "7-March-2019",
      |  "gcp_price_list": {
      |    "sustained_use_base": 0.25,
      |    "sustained_use_tiers": {
      |      "0.25": 1.0,
      |      "0.50": 0.8,
      |      "0.75": 0.6,
      |      "1.0": 0.4
      |    },
      |    "CP-COMPUTEENGINE-OS": {
      |      "win": {
      |        "low": 0.02,
      |        "high": 0.04,
      |        "cores": "shared",
      |        "percore": true
      |      },
      |      "windows-server-core": null,
      |      "rhel": {
      |        "low": 0.06,
      |        "high": 0.13,
      |        "cores": "4",
      |        "percore": false
      |      },
      |      "rhel-sap": {
      |        "low": 0.06,
      |        "high": 0.13,
      |        "cores": "4",
      |        "percore": false
      |      },
      |      "rhel-sap-ha": {
      |        "low": 0.10,
      |        "high": 0.225,
      |        "cores": "4",
      |        "percore": false
      |      },
      |      "suse": {
      |        "low": 0.02,
      |        "high": 0.11,
      |        "cores": "shared",
      |        "percore": false
      |      },
      |      "suse-sap": {
      |        "low": 0.17,
      |        "high": 0.34,
      |        "highest": 0.41,
      |        "cores": "2",
      |        "percore": false
      |      },
      |      "sql-standard": {
      |        "low": 0.1645,
      |        "high": 0.1645,
      |        "cores": "4",
      |        "percore": true
      |      },
      |      "sql-web": {
      |        "low": 0.011,
      |        "high": 0.011,
      |        "cores": "4",
      |        "percore": true
      |      },
      |      "sql-enterprise": {
      |        "low": 0.399,
      |        "high": 0.399,
      |        "cores": "4",
      |        "percore": true
      |      }
      |    },
      |    "CP-COMPUTEENGINE-STORAGE-PD-CAPACITY": {
      |      "us": 0.04,
      |      "us-central1": 0.04,
      |      "us-east1": 0.04,
      |      "us-east4": 0.044,
      |      "us-west1": 0.04,
      |      "us-west2": 0.048,
      |      "europe": 0.04,
      |      "europe-west1": 0.04,
      |      "europe-west2": 0.048,
      |      "europe-west3": 0.048,
      |      "europe-west4": 0.044,
      |      "europe-west6": 0.050,
      |      "europe-north1": 0.044,
      |      "northamerica-northeast1": 0.044,
      |      "asia-east": 0.04,
      |      "asia-east1": 0.04,
      |      "asia-east2": 0.050,
      |      "asia-northeast": 0.052,
      |      "asia-northeast1": 0.052,
      |      "asia-northeast2": 0.052,
      |      "asia-southeast": 0.044,
      |      "australia-southeast1": 0.054,
      |      "australia": 0.054,
      |      "southamerica-east1": 0.060,
      |      "asia-south1": 0.048
      |    },
      |    "CP-COMPUTEENGINE-PD-IO-REQUEST": {
      |      "us": 0.0
      |    },
      |    "CP-COMPUTEENGINE-VMIMAGE-N1-ULTRAMEM-80-PREEMPTIBLE": {
      |      "us": 2.6622,
      |      "us-central1": 2.6622,
      |      "us-east1": 2.6622,
      |      "us-east4": 0,
      |      "us-west1": 2.6622,
      |      "us-west2": 2.8875,
      |      "europe": 2.9128,
      |      "europe-west1": 2.9128,
      |      "europe-west2": 0,
      |      "europe-west3": 3.0873,
      |      "europe-west4": 0,
      |      "europe-west6": 0,
      |      "europe-north1": 0,
      |      "northamerica-northeast1": 2.6464,
      |      "asia": 0,
      |      "asia-east": 0,
      |      "asia-east1": 0,
      |      "asia-east2": 0,
      |      "asia-northeast": 3.3363,
      |      "asia-northeast1": 3.3363,
      |      "asia-northeast2": 3.3363,
      |      "asia-southeast": 2.9616,
      |      "australia-southeast1": 3.4068,
      |      "australia": 3.4068,
      |      "southamerica-east1": 3.8185,
      |      "asia-south1": 0,
      |      "cores": "80",
      |      "memory": "1922",
      |      "gceu": 220,
      |      "maxNumberOfPd": 16,
      |      "maxPdSize": 64,
      |      "ssd": [
      |        0,
      |        1,
      |        2,
      |        3,
      |        4,
      |        5,
      |        6,
      |        7,
      |        8
      |      ]
      |    },
      |    "CP-COMPUTEENGINE-VMIMAGE-N1-ULTRAMEM-160-PREEMPTIBLE": {
      |      "us": 5.3244,
      |      "us-central1": 5.3244,
      |      "us-east1": 5.3244,
      |      "us-east4": 0,
      |      "us-west1": 5.3244,
      |      "us-west2": 5.7751,
      |      "europe": 5.8255,
      |      "europe-west1": 5.8255,
      |      "europe-west2": 0,
      |      "europe-west3": 6.1746,
      |      "europe-west4": 0,
      |      "europe-west6": 0,
      |      "europe-north1": 0,
      |      "northamerica-northeast1": 5.2929,
      |      "asia": 0,
      |      "asia-east": 0,
      |      "asia-east1": 0,
      |      "asia-east2": 0,
      |      "asia-northeast": 6.6726,
      |      "asia-northeast1": 6.6726,
      |      "asia-northeast2": 6.6726,
      |      "asia-southeast": 5.9232,
      |      "australia-southeast1": 6.8135,
      |      "australia": 6.8135,
      |      "southamerica-east1": 7.6369,
      |      "asia-south1": 0,
      |      "cores": "160",
      |      "memory": "3844",
      |      "gceu": 440,
      |      "maxNumberOfPd": 16,
      |      "maxPdSize": 64,
      |      "ssd": [
      |        0,
      |        1,
      |        2,
      |        3,
      |        4,
      |        5,
      |        6,
      |        7,
      |        8
      |      ]
      |    },
      |    "CP-COMPUTEENGINE-INTERNET-EGRESS-NA-NA": {
      |      "tiers": {
      |        "1024": 0.12,
      |        "10240": 0.11,
      |        "92160": 0.08
      |      }
      |    },
      |    "CP-COMPUTEENGINE-INTERNET-EGRESS-APAC-APAC": {
      |      "tiers": {
      |        "1024": 0.12,
      |        "10240": 0.11,
      |        "92160": 0.08
      |      }
      |    },
      | }
      |}""".stripMargin
}