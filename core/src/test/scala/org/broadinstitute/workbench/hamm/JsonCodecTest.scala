package org.broadinstitute.workbench.hamm

import io.circe.parser._
import JsonCodec._

import scala.concurrent.duration.Duration

object JsonCodecTest extends HammTestSuite {
  test("metadataResponseDecoder should be able to decode MetadataResponse"){
    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def toInstant(time: String): Instant = formatter.parse(time).toInstant
    val res = for {
      json <- parse(sampleTest)
      r <- json.as[MetadataResponse]
    } yield {
      val expectedResponse = MetadataResponse(
        List(Call(
          RuntimeAttributes(CpuNumber(1), Disks(DiskName("local-disk"), DiskSize(1), DiskType.stringToDiskType("HDD")), BootDiskSizeGb(10), PreemptibleAttemptsAllowed(3)),
          List(ExecutionEvent(ExecutionEventDescription("delocalizing-files"),       Instant.parse("2019-01-02T22:14:05.438689657Z"), Instant.parse("2019-01-02T22:14:09.779343193Z")),
               ExecutionEvent(ExecutionEventDescription("UpdatingJobStore"),         Instant.parse("2019-01-02T22:14:39.825Z"),       Instant.parse("2019-01-02T22:14:40.799Z")),
               ExecutionEvent(ExecutionEventDescription("ok"),                       Instant.parse("2019-01-02T22:14:09.779343193Z"), Instant.parse("2019-01-02T22:14:10Z")),
               ExecutionEvent(ExecutionEventDescription("waiting for quota"),        Instant.parse("2019-01-02T22:11:04Z"),           Instant.parse("2019-01-02T22:11:27Z")),
               ExecutionEvent(ExecutionEventDescription("RequestingExecutionToken"), Instant.parse("2019-01-02T22:10:13.687Z"),       Instant.parse("2019-01-02T22:10:13.979Z")),
               ExecutionEvent(ExecutionEventDescription("RunningJob"),               Instant.parse("2019-01-02T22:11:02.884Z"),       Instant.parse("2019-01-02T22:11:04Z")),
               ExecutionEvent(ExecutionEventDescription("UpdatingCallCache"),        Instant.parse("2019-01-02T22:14:39.160Z"),       Instant.parse("2019-01-02T22:14:39.825Z")),
               ExecutionEvent(ExecutionEventDescription("pulling-image"),            Instant.parse("2019-01-02T22:12:47.780575142Z"), Instant.parse("2019-01-02T22:12:52.779343466Z")),
               ExecutionEvent(ExecutionEventDescription("cromwell poll interval"),   Instant.parse("2019-01-02T22:14:10Z"),           Instant.parse("2019-01-02T22:14:39.160Z")),
               ExecutionEvent(ExecutionEventDescription("localizing-files"),         Instant.parse("2019-01-02T22:12:52.779343466Z"), Instant.parse("2019-01-02T22:14:04.589980901Z")),
               ExecutionEvent(ExecutionEventDescription("Pending"),                  Instant.parse("2019-01-02T22:10:13.686Z"),       Instant.parse("2019-01-02T22:10:13.687Z")),
               ExecutionEvent(ExecutionEventDescription("start"),                    Instant.parse("2019-01-02T22:12:46.103634373Z"), Instant.parse("2019-01-02T22:12:47.780575142Z")),
               ExecutionEvent(ExecutionEventDescription("WaitingForValueStore"),     Instant.parse("2019-01-02T22:10:13.979Z"),       Instant.parse("2019-01-02T22:10:13.979Z")),
               ExecutionEvent(ExecutionEventDescription("initializing VM"),          Instant.parse("2019-01-02T22:11:27Z"),           Instant.parse("2019-01-02T22:12:46.103634373Z")),
               ExecutionEvent(ExecutionEventDescription("running-docker"),           Instant.parse("2019-01-02T22:14:04.589980901Z"), Instant.parse("2019-01-02T22:14:05.438689657Z")),
               ExecutionEvent(ExecutionEventDescription("CheckingCallCache"),        Instant.parse("2019-01-02T22:11:02.874Z"),       Instant.parse("2019-01-02T22:11:02.884Z")),
               ExecutionEvent(ExecutionEventDescription("PreparingJob"),             Instant.parse("2019-01-02T22:10:13.979Z"),       Instant.parse("2019-01-02T22:11:02.874Z"))),
          false,
          true,
          Region.stringToRegion("us-central1-c"),
          Status.stringToStatus("Success"),
          MachineType.F1Micro,
          BackEnd.stringToBackEnd("JES"),
          Attempt(1))),
        Instant.parse("2019-01-02T22:10:07.088Z"),
        Instant.parse("2019-01-02T22:14:47.266Z")
      )
      assertEquals(r, expectedResponse)
    }

    res.fold[Unit](e => throw e, identity)
  }

  val sampleTest: String =
    """
      |{
      |  "actualWorkflowLanguage": "WDL",
      |  "actualWorkflowLanguageVersion": "draft-2",
      |  "calls": {
      |    "echo_strings.echo_files": [
      |      {
      |        "attempt": 1,
      |        "backend": "JES",
      |        "backendLabels": {
      |          "cromwell-workflow-id": "cromwell-c5b6ee46-1f09-4830-91a8-fd814866d664",
      |          "wdl-task-name": "echo-files"
      |        },
      |        "backendLogs": {
      |          "log": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/echo_strings/c5b6ee46-1f09-4830-91a8-fd814866d664/call-echo_files/echo_files.log"
      |        },
      |        "backendStatus": "Success",
      |        "callCaching": {
      |          "allowResultReuse": true,
      |          "effectiveCallCachingMode": "ReadAndWriteCache",
      |          "hashes": {
      |            "backend name": "5BAA79C7C5A573C899A61D342AA00484",
      |            "command template": "488271A0C04C1E92591820C624D26E27",
      |            "input": {
      |              "File input1": "1oAaOw==",
      |              "File input10": "1oAaOw==",
      |              "File input2": "1oAaOw==",
      |              "File input3": "1oAaOw==",
      |              "File input4": "1oAaOw==",
      |              "File input5": "1oAaOw==",
      |              "File input6": "1oAaOw==",
      |              "File input7": "1oAaOw==",
      |              "File input8": "1oAaOw==",
      |              "File input9": "1oAaOw==",
      |              "Float ref_size": "DFAA538171FB8C231EFA25533565981B"
      |            },
      |            "input count": "6512BD43D9CAA6E02C990B0A82652DCA",
      |            "output count": "C4CA4238A0B923820DCC509A6F75849B",
      |            "output expression": {
      |              "String out": "0183144CF6617D5341681C6B2F756046"
      |            },
      |            "runtime attribute": {
      |              "continueOnReturnCode": "CFCD208495D565EF66E7DFF9F98764DA",
      |              "docker": "7320B4F09D0D81F497D3EF0CC08C79A6",
      |              "failOnStderr": "68934A3E9455FA72420237EB05902327"
      |            }
      |          },
      |          "hit": false,
      |          "result": "Cache Miss"
      |        },
      |        "callRoot": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/echo_strings/c5b6ee46-1f09-4830-91a8-fd814866d664/call-echo_files",
      |        "commandLine": "# sleep for 1 hour\n# sleep 3600\n\necho \"result: 0.024049579999999997\"",
      |        "dockerImageUsed": "ubuntu@sha256:868fd30a0e47b8d8ac485df174795b5e2fe8a6c8f056cc707b232d65b8a1ab68",
      |        "end": "2019-01-02T22:14:40.800Z",
      |        "executionEvents": [
      |          {
      |            "description": "delocalizing-files",
      |            "endTime": "2019-01-02T22:14:09.779343193Z",
      |            "startTime": "2019-01-02T22:14:05.438689657Z"
      |          },
      |          {
      |            "description": "UpdatingJobStore",
      |            "endTime": "2019-01-02T22:14:40.799Z",
      |            "startTime": "2019-01-02T22:14:39.825Z"
      |          },
      |          {
      |            "description": "ok",
      |            "endTime": "2019-01-02T22:14:10Z",
      |            "startTime": "2019-01-02T22:14:09.779343193Z"
      |          },
      |          {
      |            "description": "waiting for quota",
      |            "endTime": "2019-01-02T22:11:27Z",
      |            "startTime": "2019-01-02T22:11:04Z"
      |          },
      |          {
      |            "description": "RequestingExecutionToken",
      |            "endTime": "2019-01-02T22:10:13.979Z",
      |            "startTime": "2019-01-02T22:10:13.687Z"
      |          },
      |          {
      |            "description": "RunningJob",
      |            "endTime": "2019-01-02T22:11:04Z",
      |            "startTime": "2019-01-02T22:11:02.884Z"
      |          },
      |          {
      |            "description": "UpdatingCallCache",
      |            "endTime": "2019-01-02T22:14:39.825Z",
      |            "startTime": "2019-01-02T22:14:39.160Z"
      |          },
      |          {
      |            "description": "pulling-image",
      |            "endTime": "2019-01-02T22:12:52.779343466Z",
      |            "startTime": "2019-01-02T22:12:47.780575142Z"
      |          },
      |          {
      |            "description": "cromwell poll interval",
      |            "endTime": "2019-01-02T22:14:39.160Z",
      |            "startTime": "2019-01-02T22:14:10Z"
      |          },
      |          {
      |            "description": "localizing-files",
      |            "endTime": "2019-01-02T22:14:04.589980901Z",
      |            "startTime": "2019-01-02T22:12:52.779343466Z"
      |          },
      |          {
      |            "description": "Pending",
      |            "endTime": "2019-01-02T22:10:13.687Z",
      |            "startTime": "2019-01-02T22:10:13.686Z"
      |          },
      |          {
      |            "description": "start",
      |            "endTime": "2019-01-02T22:12:47.780575142Z",
      |            "startTime": "2019-01-02T22:12:46.103634373Z"
      |          },
      |          {
      |            "description": "WaitingForValueStore",
      |            "endTime": "2019-01-02T22:10:13.979Z",
      |            "startTime": "2019-01-02T22:10:13.979Z"
      |          },
      |          {
      |            "description": "initializing VM",
      |            "endTime": "2019-01-02T22:12:46.103634373Z",
      |            "startTime": "2019-01-02T22:11:27Z"
      |          },
      |          {
      |            "description": "running-docker",
      |            "endTime": "2019-01-02T22:14:05.438689657Z",
      |            "startTime": "2019-01-02T22:14:04.589980901Z"
      |          },
      |          {
      |            "description": "CheckingCallCache",
      |            "endTime": "2019-01-02T22:11:02.884Z",
      |            "startTime": "2019-01-02T22:11:02.874Z"
      |          },
      |          {
      |            "description": "PreparingJob",
      |            "endTime": "2019-01-02T22:11:02.874Z",
      |            "startTime": "2019-01-02T22:10:13.979Z"
      |          }
      |        ],
      |        "executionStatus": "Done",
      |        "inputs": {
      |          "input1": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input10": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input2": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input3": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input4": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input5": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input6": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input7": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input8": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "input9": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |          "ref_size": 0.024049579999999997
      |        },
      |        "jes": {
      |          "endpointUrl": "https://genomics.googleapis.com/",
      |          "executionBucket": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8",
      |          "googleProject": "perf-test-a",
      |          "instanceName": "ggp-4988572594778034002",
      |          "machineType": "us-central1-c/f1-micro",
      |          "zone": "us-central1-c"
      |        },
      |        "jobId": "operations/EPPx-oSBLRjSnvm_gou-nUUghYCXpboJKg9wcm9kdWN0aW9uUXVldWU",
      |        "labels": {
      |          "cromwell-workflow-id": "cromwell-c5b6ee46-1f09-4830-91a8-fd814866d664",
      |          "wdl-task-name": "echo_files"
      |        },
      |        "outputs": {
      |          "out": "result: 0.024049579999999997"
      |        },
      |        "preemptible": true,
      |        "returnCode": 0,
      |        "runtimeAttributes": {
      |          "bootDiskSizeGb": "10",
      |          "continueOnReturnCode": "0",
      |          "cpu": "1",
      |          "cpuMin": "1",
      |          "disks": "local-disk 1 HDD",
      |          "docker": "ubuntu:latest",
      |          "failOnStderr": "false",
      |          "maxRetries": "0",
      |          "memory": "0.1 GB",
      |          "memoryMin": "2.048 GB",
      |          "noAddress": "false",
      |          "preemptible": "3",
      |          "zones": "us-central1-b,us-central1-c,us-central1-f"
      |        },
      |        "shardIndex": -1,
      |        "start": "2019-01-02T22:10:13.686Z",
      |        "stderr": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/echo_strings/c5b6ee46-1f09-4830-91a8-fd814866d664/call-echo_files/echo_files-stderr.log",
      |        "stdout": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/echo_strings/c5b6ee46-1f09-4830-91a8-fd814866d664/call-echo_files/echo_files-stdout.log"
      |      }
      |    ]
      |  },
      |  "end": "2019-01-02T22:14:47.266Z",
      |  "id": "c5b6ee46-1f09-4830-91a8-fd814866d664",
      |  "inputs": {
      |    "echo_strings.echo_files.input1": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input10": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input2": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input3": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input4": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input5": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input6": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input7": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input8": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt",
      |    "echo_strings.echo_files.input9": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt"
      |  },
      |  "labels": {
      |    "cromwell-workflow-id": "cromwell-c5b6ee46-1f09-4830-91a8-fd814866d664"
      |  },
      |  "outputs": {
      |    "echo_strings.echo_files.out": "result: 0.024049579999999997"
      |  },
      |  "start": "2019-01-02T22:10:07.088Z",
      |  "status": "Succeeded",
      |  "submission": "2019-01-02T22:10:03.665Z",
      |  "submittedFiles": {
      |    "inputs": "{\"echo_strings.echo_files.input1\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input5\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input2\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input10\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input6\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input9\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input3\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input7\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input8\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\",\"echo_strings.echo_files.input4\":\"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/sampleA.txt\"}",
      |    "labels": "{}",
      |    "options": "{\n  \"default_runtime_attributes\": {\n    \"zones\": \"us-central1-b us-central1-c us-central1-f\"\n  },\n  \"user_service_account_json\": \"cleared\",\n  \"google_project\": \"perf-test-a\",\n  \"auth_bucket\": \"gs://cromwell-auth-perf-test-a\",\n  \"google_compute_service_account\": \"pet-106851910636374754604@perf-test-a.iam.gserviceaccount.com\",\n  \"final_workflow_log_dir\": \"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/workflow.logs\",\n  \"account_name\": \"harry.potter@test.firecloud.org\",\n  \"jes_gcs_root\": \"gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8\",\n  \"read_from_cache\": true\n}",
      |    "root": "",
      |    "workflow": "task echo_files {\n  File input1\n  File input2\n  File input3\n  File input4\n  File input5\n  File input6\n  File input7\n  File input8\n  File input9\n  File input10\n  \n  Float ref_size = size(input1, \"GB\") + size(input2, \"GB\") + size(input3, \"GB\") + size(input4, \"GB\") + size(input5, \"GB\") + size(input6, \"GB\") + size(input7, \"GB\") + size(input8, \"GB\") + size(input9, \"GB\") + size(input10, \"GB\")\n  \n  output {\n    String out = read_string(stdout())\n  }\n\n  command {\n    # sleep for 1 hour\n    # sleep 3600\n    \n    echo \"result: ${ref_size}\"\n  }\n\n  runtime {\n    docker: \"ubuntu:latest\"\n    cpu: \"1\"\n    memory: \"0.1 GB\"\n    preemptible: 3\n    disks: \"local-disk 1 HDD\"\n    bootDiskSizeGb: 10\n  }\n}\n\nworkflow echo_strings {\n  call echo_files\n}",
      |    "workflowUrl": ""
      |  },
      |  "workflowLog": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/workflow.logs/workflow.c5b6ee46-1f09-4830-91a8-fd814866d664.log",
      |  "workflowName": "echo_strings",
      |  "workflowRoot": "gs://fc-d28aeae4-30cc-46ca-986d-2ffda410f47c/e9e9d8ef-7ca0-4c69-abc2-e93e959c81a8/echo_strings/c5b6ee46-1f09-4830-91a8-fd814866d664/"
      |}
    """.stripMargin
}
