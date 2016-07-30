#!/usr/bin/env bash
echo "Getting required Android.jar..."
rm -rf "local-repo"
mkdir -p "local-repo"
rm -rf "tmp"
mkdir -p "tmp"
pushd "tmp"
curl "https://chromium.googlesource.com/android_tools/+/master/sdk/platforms/android-23/android.jar?format=TEXT" | base64 -d > android.jar

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
zip --delete android.jar android/content/Context.class
zip --delete android.jar android/net/Uri.class
zip --delete android.jar 'android/net/Uri$Builder.class'
zip --delete android.jar android/os/Environment.class
zip --delete android.jar android/text/format/Formatter.class

echo "Installing to local repo..."
popd
mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=tmp/android.jar -DgroupId=android \
                         -DartifactId=android -Dversion=1.0 \
                         -Dpackaging=jar -DlocalRepositoryPath=local-repo

echo "Cleaning up..."
rm -rf "tmp"

echo "Done!"