#!/usr/bin/env bash
shopt -s dotglob

BASE="tachiyomi/app/src/main/java/eu/kanade/tachiyomi"
TARGET="../Tachiyomi-App/src/main/java/eu/kanade/tachiyomi"

function packageNameToPath {
    echo "${1//.//}"
}

function keepFile() {
    echo "Keeping file: $1.$2"
    PP="$(packageNameToPath "$1")"
    mkdir -p "$TARGET/$PP/"
    cp "$BASE/$PP/$2" "$TARGET/$PP/"
}

function keepFolder() {
    echo "Keeping folder: $1"
    PP="$(packageNameToPath "$1")"
    mkdir -p "$TARGET/$PP"
    cp -r $BASE/$PP/* "$TARGET/$PP/"
}

function excludeFile() {
    echo "Excluding file: $1.$2"
    PP="$(packageNameToPath "$1")/$2"
    rm "$TARGET/$PP"
}

function excludeFolder() {
    echo "Excluding folder: $1"
    PP="$(packageNameToPath "$1")"
    rm -rf "$TARGET/$PP"
}

echo "Removing old source files..."
rm -rf "$TARGET"

echo "Downloading Tachiyomi source code..."
rm -rf tmp
mkdir tmp
pushd tmp
git clone https://github.com/inorichi/tachiyomi

echo "Filtering source code..."
# Include
keepFile "ui.reader" "ChapterLoader.kt"
keepFile "ui.reader" "ReaderChapter.kt"
keepFile "" "Constants.kt"
keepFile "" "AppModule.kt"
keepFolder "data"
keepFolder "util"
keepFolder "source"
keepFolder "network"

# Exclude
excludeFolder "data.glide"
excludeFolder "data.updater"
excludeFolder "data.notification"
excludeFile "util" "ContextExtensions.kt"
excludeFile "util" "GLUtil.java"
excludeFile "util" "ImageViewExtensions.kt"
excludeFile "util" "ViewExtensions.kt"
excludeFile "data.library" "LibraryUpdateJob.kt"
excludeFile "data.library" "LibraryUpdateService.kt"
excludeFile "util" "FileExtensions.kt"
excludeFile "data.download" "DownloadNotifier.kt"

echo "Downloading JunRAR source code..."
git clone "https://github.com/inorichi/junrar-android"
rm -rf "../junrarandroid/src/main/java/junrar"
mv "junrar-android/library/src/main/java/junrar" "../junrarandroid/src/main/java/"

echo "Cleaning up..."
popd
rm -rf tmp