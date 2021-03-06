[![Build Status](https://travis-ci.com/DataBiosphere/hamm.png?branch=master)](https://travis-ci.org/DataBiosphere/hamm)
[![Coverage Status](https://coveralls.io/repos/github/DataBiosphere/hamm/badge.svg)](https://coveralls.io/github/DataBiosphere/hamm)

# Try it out
## Hamm-Api-Server
* Start hamm-api-server `sbt server/run`
* Run automation tests against the running server `sbt automation/test`

## Hamm-Cost-Updater
* Start hamm-api-server `sbt costUpdater/run`
* Run automation tests against the running server `sbt automation/test`

# Publish container image to Google container registry
* Set up auth for publishing docker image to GCR
`gcloud auth configure-docker`
* Publish hamm-api-server
`sbt server/docker:publish` (or `sbt server/docker:publishLocal` for local development)
* Publish hamm-cost-updater
`sbt costUpdater/docker:publish` (or `sbt costUpdater/docker:publishLocal` for local development)

# /status
Both `hamm-cost-updater` and `hamm-api-server` provides /status endpoint.
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
docker run --name postgres -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=hamm -e POSTGRES_DB=hamm -p 5433:5432 -d postgres
```