#!/usr/bin/env bash

dir="/Users/qi/workspace/cloud-cost-management"
protoc -I=$dir/protobuf/src/main/protobuf/ --descriptor_set_out=$dir/protobuf/target/ccm.pb $dir/protobuf/src/main/protobuf/ccm.proto
# deploy cloud enpoint
gcloud endpoints services deploy $dir/protobuf/target/ccm.pb api_config.yaml
# creating kubernetes cluster
#kubectl create -f kub_service_deploy.yaml
#gcloud endpoints services deploy $dir/kubernetes/ccm.pb $dir/kubernetes/api_config.yaml
