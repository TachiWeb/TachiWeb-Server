#!/usr/bin/env bash

LINUX_WINDOWS="false"
for i in "$@" ; do
    if [[ ${i} == "--windows" ]] ; then
        LINUX_WINDOWS="true"
        break
    fi
done

# Check commands
function checkCommand() {
    command -v $1 >/dev/null 2>&1 || { echo >&2 "$1 is required but it's not installed. Aborting!"; exit 1; }
}

function printJavaBinaries() {
    echo "$(realpath "TachiServer/build/libs/$(ls TachiServer/build/libs | grep TachiServer-all)")"
}

function printNativeBinaries() {
    ls -1 bootui/tachiweb-bootstrap/dist | grep -i tachiweb- | while read x; do echo "$(realpath "bootui/tachiweb-bootstrap/dist/$x")"; done
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

YARN_COMMAND=""
if [[ ${LINUX_WINDOWS} == "true" ]]; then
    YARN_COMMAND=":bootui:yarn_distLinuxWindows"
else
    YARN_COMMAND=":bootui:yarn_dist"
fi

if [[ ${TRAVIS} == "true" ]]; then
    ./gradlew "$YARN_COMMAND" :TachiServer:fatJar || { echo 'Travis build failed!' ; exit 1; }
    echo -e "\n\n-------------> Travis build complete! <-------------"
    echo "Output files:"
    printJavaBinaries
    printNativeBinaries
else
    # Build and package server into JAR
    ./gradlew clean assemble :TachiServer:fatJar || { echo 'Java build failed!' ; exit 1; }

    # Output build info
    echo -e "\n\n-------------> Java build complete! <-------------"
    echo "Output file:"
    printJavaBinaries
    echo "Continuing to build native binaries..."

    # Package native
    ./gradlew "$YARN_COMMAND" || { echo 'Native build failed!' ; exit 1; }
    echo -e "\n\n-------------> Native build complete! <-------------"
    echo "Output files:"
    printNativeBinaries
fi
