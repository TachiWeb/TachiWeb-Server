#!/usr/bin/env bash
# Update submodules (UI components)
echo "Downloading web components..."
git submodule update --init --recursive
# Build UI components
chmod +x buildWeb.sh
./buildWeb.sh
# Init local repo
rm -rf "local-repo"
mkdir -p "local-repo"
# Get android JAR
chmod +x getAndroid.sh
./getAndroid.sh
# Get Spark
chmod +x getSpark.sh
./getSpark.sh
# Remove old build
rm -rf target
# Build and package server into JAR
mvn clean package -U