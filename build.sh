#!/usr/bin/env bash
# Update submodules (UI components)
git submodule update --init --recursive
# Build UI components
pushd src/main/resources/tachiweb-ui
bower install
popd
# Get android JAR
./getAndroid.sh
# Build and package server into JAR
mvn clean package