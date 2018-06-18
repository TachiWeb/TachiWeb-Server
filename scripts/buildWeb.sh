#!/usr/bin/env bash
echo "Building web components..."
# Old UI (TODO Remove from build)
pushd tachiwebui/src/main/resources/tachiweb-ui
bower install
npm install

echo "Removing unnecessary files..."
pushd bower_components
# Material icons (takes forever to copy all of these)
pushd material-design-icons
rm -rf action alert av communication content device editor file hardware image maps navigation notification places social toggle
popd
popd

popd

# New UI
pushd tachiwebui/src/main/resources/tachiweb-react
npm install
npm run build
popd
