#!/usr/bin/env bash
# Formatting stuff
BOLD=$(tput bold)
UNDERLINE=$(tput smul)
NORMAL=$(tput sgr0)
CYAN="\e[96m"
BLUE="\e[94m"
YELLOW="\e[93m"
GREEN="\e[92m"
GRAY_BCK="\e[100m"

echo -e "${UNDERLINE}${BOLD}${GRAY_BCK}${GREEN}Fetching latest Tachiyomi source code...${NORMAL}"

ORIGIN_ROOT="https://raw.githubusercontent.com/inorichi/tachiyomi/master"
CODE_ROOT="app/src/main/java"
CURRENT_PACKAGE=()

# Package name to path
function packageNameToPath {
    echo "${1//.//}"
}
# Quiet pushd and popd
function pushd {
    command pushd "$@" > /dev/null
}
function popd {
    command popd "$@" > /dev/null
}
# Join (http://stackoverflow.com/a/17841619/5054192)
function join { local IFS="$1"; shift; echo "$*"; }

function getFullPackageName {
    echo $(join "." "${CURRENT_PACKAGE[@]}")
}
function getFullPackagePath {
    echo $(packageNameToPath $(getFullPackageName))
}
# Go to package
function enterPackage {
    PACKAGE_NAME="$1"
    CURRENT_PACKAGE+=("$PACKAGE_NAME")
    PACKAGE_PATH=$(packageNameToPath "$PACKAGE_NAME")
    echo -e "${BOLD}${CYAN}Entering package: $(getFullPackageName)${NORMAL}"
    mkdir -p "$PACKAGE_PATH"
    pushd "$PACKAGE_PATH"
}
# Leave previous package
function leavePackage {
    echo -e "${BOLD}${BLUE}Leaving package: $(getFullPackageName)${NORMAL}"
    popd
    unset CURRENT_PACKAGE[${#CURRENT_PACKAGE[@]}-1]
}
# Download a file
function download {
    FILENAME="$1"
    wget "$ORIGIN_ROOT/$CODE_ROOT/$(getFullPackagePath)/$FILENAME" -q --show-progress -O "$FILENAME"
}

# cd to source code
cd src/main/java/
enterPackage "eu.kanade.tachiyomi"

################### ACTUAL DOWNLOADING BEGINS HERE ###################

# Get models
enterPackage "data.database.models"
    download "Chapter.kt"
    download "Manga.kt"
    download "ChapterImpl.kt"
    download "MangaImpl.kt"
    download "Category.kt"
    download "CategoryImpl.kt"
    download "MangaSync.kt"
    download "MangaSyncImpl.kt"
    download "MangaCategory.kt"
    download "MangaChapter.kt"
    download "MangaChapterHistory.kt"
leavePackage

enterPackage "data.mangasync"
    download "MangaSyncManager.kt"
    download "MangaSyncService.kt"

    enterPackage "anilist"
        download "Anilist.kt"
        download "AnilistApi.kt"
        download "AnilistInterceptor.kt"
        enterPackage "model"
            download "ALManga.kt"
            download "ALUserLists.kt"
            download "ALUserManga.kt"
            download "OAuth.kt"
        leavePackage
    leavePackage

    enterPackage "myanimelist"
        download "MyAnimeList.kt"
    leavePackage

leavePackage

# Get source stuff
enterPackage "data.source"
    download "Source.kt"
    download "Language.kt"
    download "SourceManager.kt"
leavePackage

enterPackage "data.source.model"
    download "Page.kt"
    download "MangasPage.kt"
leavePackage

enterPackage "data.source.online"
    # Custom impl
    #download "OnlineSource.kt"
    download "LoginSource.kt"
    download "ParsedOnlineSource.kt"
    download "YamlOnlineSource.kt"
    download "YamlOnlineSourceMappings.kt"

    # Download all online sources but if inorichi decides to add sources in the online package directly, we are screwed, we will deal with that when it actually happens (probably won't, inorichi is smart)
    svn export https://github.com/inorichi/tachiyomi.git/trunk/app/src/main/java/eu/kanade/tachiyomi/data/source/online
    pushd "online"
        rm * # Delete all files
        cp -r * ../ # Copy out all folders
    popd
    rm -rf "online" # Delete online package
leavePackage

# Get some network utils
enterPackage "data.network"
    download "OkHttpExtensions.kt"
    download "Requests.kt"
    download "ProgressListener.kt"
    download "NetworkHelper.kt"
    download "PersistentCookieStore.kt"
    download "PersistentCookieJar.kt"
    download "ProgressResponseBody.kt"
leavePackage

# Get backup stuff (NOT USED)
enterPackage "data.backup"
    # Custom impl
    #download "BackupManager.kt"
leavePackage

enterPackage "data.backup.serializer"
    download "IntegerSerializer.kt"
    download "IdExclusion.kt"
leavePackage

# Get some UI stuff
enterPackage "ui.reader"
    download "ReaderChapter.kt"
leavePackage

# Get cache stuff
enterPackage "data.cache"
    download "ChapterCache.kt"
    download "CoverCache.kt"
leavePackage

# Get some utils
enterPackage "util"
    download "JsoupExtensions.kt"
    download "UrlUtil.java"
    download "DiskUtils.java"
    download "ChapterRecognition.kt"
    download "DynamicConcurrentMergeOperator.java"
    download "RetryWithDelay.kt"
leavePackage

# Download stuff
enterPackage "data.download.model"
    download "Download.kt"
    download "DownloadQueue.kt"
leavePackage

echo -e "${UNDERLINE}${BOLD}${GRAY_BCK}${GREEN}Done!${NORMAL}"
