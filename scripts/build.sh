#!/usr/bin/env bash

# Check commands
function checkCommand() {
    command -v $1 >/dev/null 2>&1 || { echo >&2 "$1 is required but it's not installed. Aborting!"; exit 1; }
}

#checkCommand bower
checkCommand java
checkCommand npm
checkCommand yarn
checkCommand curl
checkCommand zip
checkCommand unzip
checkCommand grep
checkCommand realpath
checkCommand mvn

# Build UI components
#chmod +x scripts/buildWeb.sh
#scripts/buildWeb.sh
# Init local repo
rm -rf "local-repo"
mkdir -p "local-repo"
rm -rf "libs"
mkdir -p "libs"
# Get android JAR
chmod +x scripts/getAndroid.sh
./scripts/getAndroid.sh
# Get Android libraries
chmod +x scripts/getAndroidLib.sh
./scripts/getAndroidLib.sh
# Remove old build
rm -rf target
# Build and package server into JAR
./gradlew clean assemble :TachiServer:fatJar

# Output build info
echo -e "\n\n-------------> Java Build complete! <-------------"
echo "Output file: $(realpath "TachiServer/build/libs/$(ls TachiServer/build/libs | grep TachiServer-all)")"
echo "Continuing to build native binaries..."

# Package native
./gradlew :bootui:yarn_dist
echo -e "\n\n-------------> Native Build complete! <-------------"
echo "Output files:"
ls bootui/tachiweb-bootstrap/dist -1 | grep tachiweb- | while read x; do echo "$(realpath "bootui/tachiweb-bootstrap/dist/$x")"; done
