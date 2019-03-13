#!/usr/bin/env bash

trim() {
    local var="$*"
    # remove leading whitespace characters
    var="${var#"${var%%[![:space:]]*}"}"
    # remove trailing whitespace characters
    var="${var%"${var##*[![:space:]]}"}"
    echo -n "$var"
}

mkdir -p build/api-client
pushd build/api-client

SCHEMA_LOCATION="../../TachiServer/src/main/resources/openapi.json"
SCHEMA_VERSION="$(trim "$(cat "$SCHEMA_LOCATION" | jq -r ".info.version")")"
REMOTE_VERSION="$(trim "$(npm view @tachiweb/api-client version)")"

echo "API client versions: Remote=$REMOTE_VERSION, Local=$SCHEMA_VERSION"

# Rebuild and publish only if API version changed
if [[ "$REMOTE_VERSION" != "$SCHEMA_VERSION" ]]; then
    echo "Version mismatch, rebuilding API client!"

    echo "Downloading OpenAPI Generator..."
    wget http://central.maven.org/maven2/org/openapitools/openapi-generator-cli/4.0.0-beta2/openapi-generator-cli-4.0.0-beta2.jar -O /tmp/openapi-generator-cli.jar
    java -jar /tmp/openapi-generator-cli.jar generate -i "$SCHEMA_LOCATION" -g javascript-flowtyped

    echo "Building API client..."
    cat package.json | jq ".version=\"$SCHEMA_VERSION\" | .license=\"Apache-2.0\" | .name=\"@tachiweb/api-client\" | .repository=\"https://github.com/TachiWeb/TachiWeb-Server\"" > package.json.out
    mv package.json.out package.json
    sed -i -e 's/tachi_web_api@1.0.0/@tachiweb\/api-client/g' README.md

    npm install
    npm run build

    echo "Publishing API client..."
    npm publish --access public
fi

popd
