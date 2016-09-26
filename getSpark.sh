#!/usr/bin/env bash
echo "Checking if local repo initialized..."
if [ ! -d "local-repo" ]; then
    echo "Local repo not initialized!"
    exit 1
fi
echo "Downloading Spark 2.6-SNAPSHOT..."
rm -rf "tmp"
mkdir -p "tmp"
pushd "tmp"
git clone https://github.com/perwendel/spark

pushd "spark"
echo "Reverting repo to known state..."
git reset --hard 4a6a3888d89c0070268d827d060f660209bdc07b

echo "Building Spark..."
mvn clean package -DskipTests -U
popd
popd
echo "Installing to local repo..."
mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=tmp/spark/target/spark-core-2.6-SNAPSHOT.jar -DgroupId=com.sparkjava \
                         -DartifactId=spark-core -Dversion=2.6-SNAPSHOT \
                         -Dpackaging=jar -DlocalRepositoryPath=local-repo

echo "Cleaning up..."
rm -rf "tmp"

echo "Done!"