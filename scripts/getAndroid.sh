#!/usr/bin/env bash
echo "Checking if library folder initialized..."
if [ ! -d "libs" ]; then
    echo "Library folder not initialized!"
    exit 1
fi
echo "Checking if local repo initialized..."
if [ ! -d "local-repo" ]; then
    echo "Local repo not initialized!"
    exit 1
fi
echo "Getting required Android.jar..."
rm -rf "tmp"
mkdir -p "tmp"
pushd "tmp"
curl "https://android.googlesource.com/platform/prebuilts/sdk/+/master/27/android.jar?format=TEXT" | base64 -d > android.jar

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
zip --delete android.jar android/app/Application.class
zip --delete android.jar android/app/Service.class
zip --delete android.jar android/net/Uri.class
zip --delete android.jar 'android/net/Uri$Builder.class'
zip --delete android.jar android/os/Environment.class
zip --delete android.jar android/text/format/Formatter.class
zip --delete android.jar android/text/Html.class

# Dedup overriden Android classes
ABS_JAR="$(realpath android.jar)"
function dedup() {
    pushd "$1"
    CLASSES="$(find * - type f)"
    echo "$CLASSES" | while read class
    do
        NAME="${class%.*}"
        echo "Processing class: $NAME"
        zip --delete "$ABS_JAR" "$NAME.class" "$NAME\$*.class" "${NAME}Kt.class" "${NAME}Kt\$*.class"
    done
    popd
}

pushd ..
dedup AndroidCompat/src/main/java
dedup junrarandroid/src/main/java
dedup TachiServer/src/main/java
dedup Tachiyomi-App/src/main/java
dedup Tachiyomi-App/src/compat/java
popd

popd
echo "Copying Android.jar to library folder..."
mkdir -p libs/android
cp tmp/android.jar libs/android/

function prepareSupportAnnotations() {
    echo "Getting required support-annotations-$1.jar..."
    curl "https://dl.google.com/dl/android/maven2/com/android/support/support-annotations/$1/support-annotations-$1.jar" > "tmp/support-annotations-$1.jar"

    echo "Installing support-annotations-$1.jar to local repo..."
    mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                             -Dfile=tmp/support-annotations-$1.jar -DgroupId=com.android.support \
                             -DartifactId=support-annotations -Dversion=$1 \
                             -Dpackaging=jar -DlocalRepositoryPath=local-repo
}

prepareSupportAnnotations "23.4.0"
prepareSupportAnnotations "25.0.1"
prepareSupportAnnotations "26.0.1"

echo "Cleaning up..."
rm -rf "tmp"

echo "Done!"