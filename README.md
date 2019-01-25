[![Build Status](https://travis-ci.com/DataBiosphere/hamm.png?branch=master)](https://travis-ci.org/broadinstitute/cloud-cost-management)
[![Coverage Status](https://coveralls.io/repos/github/broadinstitute/cloud-cost-management/badge.svg?branch=master)](https://coveralls.io/github/broadinstitute/cloud-cost-management?branch=master)
TODO: fix coverall badge

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
curl -k https://localhost/status
{"scalaVersion":"2.12.7","sbtVersion":"1.2.8","gitCommit":"350b74fc073550b1262609f918583eae10774ecc","buildTime":"2019-01-05T11:33:00.564"}%
```

# Development

## Using git secrets
Make sure git secrets is installed:
```bash
brew install git-secrets
```
Ensure git-secrets is run:
<i>If you use the rsync script to run locally you can skip this step</i>
```bash
cp -r hooks/ .git/hooks/
chmod 755 .git/hooks/apply-git-secrets.sh
```

## Connecting to Cloud SQL locally
https://codelabs.developers.google.com/codelabs/cloud-postgresql-gke-memegen/#5

* Download cloud_sql_proxy: `curl -o cloud_sql_proxy https://dl.google.com/cloudsql/cloud_sql_proxy.darwin.amd64`

* ./cloud_sql_proxy -instances=[INSTANCE_CONNECTION_NAME]=tcp:5432 -credential_file=key.json &

## Use postgres container for local development
```bash
docker run --name postgres -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=ccm -e POSTGRES_DB=cmm -p 5433:5432 -d postgres
```


Maybe TODO
* Deployment
- Deploy docker image to google with cloud build
* Set up automation tests in CI (deploy a server and then run automation tests against that)
* Publish client jar
* Config sentry DSN
