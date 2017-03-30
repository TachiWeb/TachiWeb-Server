#!/usr/bin/env bash
# Build UI components
chmod +x scripts/buildWeb.sh
scripts/buildWeb.sh
# Init local repo
rm -rf "local-repo"
mkdir -p "local-repo"
# Get android JAR
chmod +x scripts/getAndroid.sh
./scripts/getAndroid.sh
# Get Android libraries
chmod +x scripts/getAndroidLib.sh
./scripts/getAndroidLib.sh
# Remove old build
rm -rf target
# Build and package server into JAR
mvn clean package -U