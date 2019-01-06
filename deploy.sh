#!/usr/bin/env bash

# This script only needs to be run once
# generate .pb file for creating google cloud endpoint
java -jar ~/.ivy2/cache/com.github.os72/protoc-jar/jars/protoc-jar-3.6.0.jar -I=./protobuf/src/main/protobuf/ --descriptor_set_out=./protobuf/target/ccm.pb ./protobuf/src/main/protobuf/ccm.proto

#protoc -I=./protobuf/src/main/protobuf/ --descriptor_set_out=./protobuf/target/ccm.pb ./protobuf/src/main/protobuf/ccm.proto
# deploy cloud enpoint
gcloud endpoints services deploy ./protobuf/target/ccm.pb ./kubernetes/api_config.yaml
# creating kubernetes cluster
kubectl create -f ./kubernetes/kub_service_deploy.yaml
