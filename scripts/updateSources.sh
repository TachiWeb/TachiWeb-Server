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

echo "Download Tachiyomi source code..."
rm -rf tmp
mkdir tmp
pushd tmp
git clone https://github.com/inorichi/tachiyomi

echo "Filtering source code..."
# Include
keepFile "ui.library" "LibraryAdapter.kt"
keepFile "ui.reader" "ChapterLoader.kt"
keepFolder "data"
keepFolder "util"
keepFolder "source"

# Exclude
excludeFolder "data.glide"
excludeFile "util" "ContextExtensions.kt"
excludeFile "util" "GLUtil.java"
excludeFile "util" "ImageViewExtensions.kt"
excludeFile "util" "ViewExtensions.kt"

echo "Cleaning up..."
popd
rm -rf tmp