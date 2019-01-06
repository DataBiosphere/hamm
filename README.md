[![Build Status](https://travis-ci.org/broadinstitute/cloud-cost-management.png?branch=master)](https://travis-ci.org/broadinstitute/cloud-cost-management)
[![Coverage Status](https://coveralls.io/repos/github/broadinstitute/cloud-cost-management/badge.svg?branch=master)](https://coveralls.io/github/broadinstitute/cloud-cost-management?branch=master)

# Try it out
* Start gRPC server `sbt server/run`
* Run automation tests against the running server `sbt automation/test`
* In deployed service, we expose Json/HTTP REST API in addition to gRPC. 


# APIs
* [API doc](https://endpointsportal.workbench-firestore.cloud.goog/docs/ccm.endpoints.workbench-firestore.cloud.goog/g/overview)
* Both gRPC and REST http doc is generated based on [proto3](protobuf/src/main/protobuf/ccm.proto)

# Publish grpc docker image to Google container registry
* Set up auth for publishing docker image to GCR
`gcloud auth configure-docker`
* Publish
`sbt server/docker:publish` (or `sbt server/docker:publishLocal` for local development)

# Generating cert
https://cloud.google.com/endpoints/docs/grpc/enabling-ssl
       
# /status
Information about the server
```bash
curl -k https://35.238.22.31/status
{"scalaVersion":"2.12.7","sbtVersion":"1.2.8","gitCommit":"350b74fc073550b1262609f918583eae10774ecc","buildTime":"2019-01-05T11:33:00.564"}%
```

TODO
* calculate formula
* figure out how to get service URLs properly

Maybe TODO
* Build (google cloud build)
* Deployment
- Deploy docker image to google with cloud build
* Set up automation tests in CI (deploy a server and then run automation tests against that)
* Dockerize
* Publish client jar
* Config sentry DSN

Questions for cromwell team
1. How cromwell will interact with ccm. Contract
2. cost components
