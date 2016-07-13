#!/usr/bin/env bash
echo "Getting required Android.jar..."
cd "src/main/resources/mock"
rm -f "android.jar"
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

echo "Done!"