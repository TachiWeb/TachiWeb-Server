#!/usr/bin/env bash
# Update submodules (UI components)
echo "Updating submodules..."
git submodule update --init --recursive
# Init local repo
rm -rf "local-repo"
mkdir -p "local-repo"
# Get android JAR
chmod +x scripts/getAndroid.sh
./scripts/getAndroid.sh
# Remove old build
rm -rf target
# Build and package server into JAR
mvn clean package -U