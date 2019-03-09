#!/usr/bin/env bash

mkdir -p build/api-client
pushd build/api-client

mkdir -p /tmp/openapitools
curl https://raw.githubusercontent.com/OpenAPITools/openapi-generator/master/bin/utils/openapi-generator-cli.sh > /tmp/openapitools/openapi-generator-cli
chmod u+x /tmp/openapitools/openapi-generator-cli
/tmp/openapitools/openapi-generator-cli generate -i ../../TachiServer/src/main/resources/openapi.json -g javascript-flowtyped

cat package.json | jq ".version=\"$PKG_VERSION\" | .license=\"Apache-2.0\" | .name=\"@tachiweb/api-client\" | .repository=\"https://github.com/TachiWeb/TachiWeb-Server\"" > package.json.out
mv package.json.out package.json
sed -i -e 's/tachi_web_api@1.0.0/@tachiweb\/api-client/g' README.md

npm install
npm run build

npm publish --access public

popd
