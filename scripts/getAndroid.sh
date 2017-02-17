#!/usr/bin/env bash
echo "Checking if local repo initialized..."
if [ ! -d "local-repo" ]; then
    echo "Local repo not initialized!"
    exit 1
fi
echo "Getting required Android.jar..."
rm -rf "tmp"
mkdir -p "tmp"
pushd "tmp"
curl "https://chromium.googlesource.com/android_tools/+/e429db7f48cd615b0b408cda259ffbc17d3945bb/sdk/platforms/android-24/android.jar?format=TEXT" | base64 -d > android.jar

# We need to remove any stub classes that we might use
echo "Patching JAR..."

echo "Removing org.json..."
zip --delete android.jar org/json/*

echo "Removing org.apache..."
zip --delete android.jar org/apache/*

echo "Removing org.w3c..."
zip --delete android.jar org/w3c/*

echo "Removing org.xml..."
zip --delete android.jar org/xml/*

echo "Removing org.xmlpull..."
zip --delete android.jar org/xmlpull/*

echo "Removing junit..."
zip --delete android.jar junit/*

echo "Removing javax..."
zip --delete android.jar javax/*

echo "Removing java..."
zip --delete android.jar java/*

echo "Removing overriden classes..."
zip --delete android.jar android/net/Uri.class
zip --delete android.jar 'android/net/Uri$Builder.class'
zip --delete android.jar android/os/Environment.class
zip --delete android.jar android/text/format/Formatter.class
zip --delete android.jar android/text/Html.class

echo "Installing to local repo..."
popd
mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=tmp/android.jar -DgroupId=android \
                         -DartifactId=android -Dversion=1.01 \
                         -Dpackaging=jar -DlocalRepositoryPath=local-repo

echo "Cleaning up..."
rm -rf "tmp"

echo "Done!"