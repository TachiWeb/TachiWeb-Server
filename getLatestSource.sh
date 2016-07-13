#!/usr/bin/env bash
# TODO Don't just get all this from the master branch
echo "Fetching latest Tachiyomi source code..."
# Package name to path
function packageNameToPath {
    echo "${1//.//}"
}

# cd to source code
ROOT_TACHIYOMI_PACKAGE=$(packageNameToPath "eu.kanade.tachiyomi")
cd src/main/java/
mkdir -p "$ROOT_TACHIYOMI_PACKAGE"
cd "$ROOT_TACHIYOMI_PACKAGE"

# Get models
MODELS_PACKAGE=$(packageNameToPath "data.database.models")
mkdir -p "$MODELS_PACKAGE"
pushd "$MODELS_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/Chapter.kt" -O "Chapter.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/Manga.kt" -O "Manga.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/ChapterImpl.kt" -O "ChapterImpl.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/MangaImpl.kt" -O "MangaImpl.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/Category.kt" -O "Category.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/CategoryImpl.kt" -O "CategoryImpl.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/MangaSync.kt" -O "MangaSync.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/MangaSyncImpl.kt" -O "MangaSyncImpl.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/MangaCategory.kt" -O "MangaCategory.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/database/models/MangaChapter.kt" -O "MangaChapter.kt"
popd

# Get source stuff
SOURCE_PACKAGE=$(packageNameToPath "data.source")
mkdir -p "$SOURCE_PACKAGE"
pushd "$SOURCE_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/Source.kt" -O "Source.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/Language.kt" -O "Language.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/SourceManager.kt" -O "SourceManager.kt"
popd
SOURCE_MODELS_PACKAGE=$(packageNameToPath "data.source.model")
mkdir -p "$SOURCE_MODELS_PACKAGE"
pushd "$SOURCE_MODELS_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/model/Page.kt" -O "Page.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/model/MangasPage.kt" -O "MangasPage.kt"
popd
SOURCE_ONLINE_PACKAGE=$(packageNameToPath "data.source.online")
mkdir -p "$SOURCE_ONLINE_PACKAGE"
pushd "$SOURCE_ONLINE_PACKAGE"
# Custom impl
#wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/online/OnlineSource.kt" -O "OnlineSource.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/online/LoginSource.kt" -O "LoginSource.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/online/ParsedOnlineSource.kt" -O "ParsedOnlineSource.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/online/YamlOnlineSource.kt" -O "YamlOnlineSource.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/source/online/YamlOnlineSourceMappings.kt" -O "YamlOnlineSourceMappings.kt"
# Download all online sources but if inorichi decides to add sources in the online package directly, we are screwed, we will deal with that when it actually happens (probably won't, inorichi is smart)
svn export https://github.com/inorichi/tachiyomi.git/trunk/app/src/main/java/eu/kanade/tachiyomi/data/source/online
pushd "online"
rm * # Delete all files
cp -r * ../ # Copy out all folders
popd
rm -rf "online" # Delete online package
popd

# Get some network utils
NETWORK_PACKAGE=$(packageNameToPath "data.network")
mkdir -p "$NETWORK_PACKAGE"
pushd "$NETWORK_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/OkHttpExtensions.kt" -O "OkHttpExtensions.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/Requests.kt" -O "Requests.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/ProgressListener.kt" -O "ProgressListener.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/NetworkHelper.kt" -O "NetworkHelper.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/PersistentCookieStore.kt" -O "PersistentCookieStore.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/PersistentCookieJar.kt" -O "PersistentCookieJar.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/network/ProgressResponseBody.kt" -O "ProgressResponseBody.kt"
popd

# Get backup stuff (NOT USED)
BACKUP_PACKAGE=$(packageNameToPath "data.backup")
mkdir -p "$BACKUP_PACKAGE"
pushd "$BACKUP_PACKAGE"
# Custom impl
#wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/backup/BackupManager.kt" -O "BackupManager.kt"
popd
BACKUP_SERIALIZER_PACKAGE=$(packageNameToPath "data.backup.serializer")
mkdir -p "$BACKUP_SERIALIZER_PACKAGE"
pushd "$BACKUP_SERIALIZER_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/backup/serializer/IntegerSerializer.kt" -O "IntegerSerializer.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/backup/serializer/IdExclusion.kt" -O "IdExclusion.kt"
popd

# Get some UI stuff
READER_PACKAGE=$(packageNameToPath "ui.reader")
mkdir -p "$READER_PACKAGE"
pushd "$READER_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderChapter.kt" -O "ReaderChapter.kt"
popd

# Get cache stuff
CACHE_PACKAGE=$(packageNameToPath "data.cache")
mkdir -p "$CACHE_PACKAGE"
pushd "$CACHE_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/cache/ChapterCache.kt" -O "ChapterCache.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/data/cache/CoverCache.kt" -O "CoverCache.kt"
popd

# Get some utils
UTILS_PACKAGE=$(packageNameToPath "util")
mkdir -p "$UTILS_PACKAGE"
pushd "$UTILS_PACKAGE"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/util/JsoupExtensions.kt" -O "JsoupExtensions.kt"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/util/UrlUtil.java" -O "UrlUtil.java"
wget "https://raw.githubusercontent.com/inorichi/tachiyomi/master/app/src/main/java/eu/kanade/tachiyomi/util/DiskUtils.java" -O "DiskUtils.java"
popd

echo "Done!"
