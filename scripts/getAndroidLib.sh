#!/usr/bin/env bash
# Download and convert Android libraries to Java libraries

echo "Checking if local repo initialized..."
if [ ! -d "local-repo" ]; then
    echo "Local repo not initialized!"
    exit 1
fi

function packageNameToPath {
    echo "${1//.//}"
}

function parsePackage {
    PACKAGE_PATH=$(packageNameToPath $1)
    URL="$PACKAGE_PATH/$2/$3/$2-$3.aar"
    echo "$4$URL"
}

function downloadLib {
    rm -rf "tmp"
    mkdir -p "tmp"
    cd "tmp"

    echo "Parsing package: $2"
    splitPkg=()
    IFS=':' read -r -a splitPkg <<< "$2"
    URL="$(parsePackage "${splitPkg[0]}" "${splitPkg[1]}" "${splitPkg[2]}" "$1")"
    echo "Fetching AAR from: $URL"
    JARFILE="$2.jar"
    curl "$URL" -o "$JARFILE"
    echo "Extracting classes.jar from $JARFILE"
    unzip "$JARFILE" "classes.jar"
    echo "Installing library to local repo..."
    cd ..
    mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=tmp/classes.jar "-DgroupId=${splitPkg[0]}" \
                         "-DartifactId=${splitPkg[1]}" "-Dversion=${splitPkg[2]}" \
                         -Dpackaging=jar -DlocalRepositoryPath=local-repo
    echo "Cleaning up..."
    rm -rf "tmp"
}

function downloadCentralLib {
    downloadLib "http://central.maven.org/maven2/" "$1"
}

function downloadJitpackLib {
    downloadLib "https://jitpack.io/" "$1"
}

function downloadBintrayLib {
    downloadLib "https://dl.bintray.com/$1/" "$2"
}

# Download libraries here
downloadCentralLib "com.jakewharton.timber:timber:4.5.1"
downloadJitpackLib "com.github.seven332:unifile:1.0.0"
#downloadBintrayLib "inorichi/tachiyomi" "eu.kanade.tachiyomi:extensions-library:1.0"

echo "Done!"