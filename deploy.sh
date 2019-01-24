#!/usr/bin/env bash

#protoc -I=./protobuf/src/main/protobuf/ --descriptor_set_out=./protobuf/target/ccm.pb ./protobuf/src/main/protobuf/ccm.proto
# deploy cloud enpoint
gcloud endpoints services deploy ./protobuf/target/hamm.pb ./kubernetes/api_config.yaml
# creating kubernetes cluster
kubectl create -f ./kubernetes/kub_service_deploy.yaml
