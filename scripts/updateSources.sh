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

# Enter tmp folder
rm -rf tmp
mkdir tmp
pushd tmp

echo "Removing old source files..."
rm -rf "$TARGET"

echo "Downloading Tachiyomi source code..."
# git clone https://github.com/inorichi/tachiyomi
# TODO Remove
# Pull from fork
git clone https://github.com/null-dev/tachiyomi

echo "Filtering source code..."
# Include
keepFolder "ui.reader.loader"
keepFile "ui.reader.model" "ReaderChapter.kt"
keepFile "ui.reader.model" "ReaderPage.kt"
keepFile "ui.library" "LibrarySort.kt"
keepFile "" "AppModule.kt"
keepFolder "data"
keepFolder "util"
keepFolder "source"
keepFolder "network"
keepFolder "extension"

# Exclude
excludeFolder "data.glide"
excludeFolder "data.updater"
excludeFolder "data.notification"
excludeFolder "data.sync.account"
excludeFolder "data.sync.api"
excludeFolder "data.sync.provider"
excludeFile "util" "ContextExtensions.kt"
excludeFile "util" "GLUtil.java"
excludeFile "util" "ImageViewExtensions.kt"
excludeFile "util" "ViewExtensions.kt"
excludeFile "util" "FileExtensions.kt"
excludeFile "util" "LocaleHelper.kt"
excludeFile "data.backup" "BackupCreateService.kt"
excludeFile "data.backup" "BackupCreatorJob.kt"
excludeFile "data.backup" "BackupRestoreService.kt"
excludeFile "data.library" "LibraryUpdateJob.kt"
excludeFile "data.library" "LibraryUpdateService.kt"
excludeFile "data.download" "DownloadNotifier.kt"
excludeFile "data.download" "DownloadService.kt"
excludeFile "data.sync" "LibrarySyncAdapter.kt"
excludeFile "data.sync" "LibrarySyncService.kt"
excludeFile "data.sync" "LibrarySyncManager.kt"
excludeFile "extension.util" "ExtensionInstallReceiver.kt"
excludeFile "extension.util" "ExtensionInstaller.kt"
excludeFile "extension.util" "ExtensionInstallActivity.kt"

echo "Downloading JunRAR source code..."
git clone "https://github.com/inorichi/junrar-android"
rm -rf "../junrarandroid/src/main/java/junrar"
mv "junrar-android/library/src/main/java/junrar" "../junrarandroid/src/main/java/"

echo "Cleaning up..."
popd
rm -rf tmp