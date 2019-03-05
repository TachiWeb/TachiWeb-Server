#!/usr/bin/env bash

mkdir -p ~/.ssh
echo "$SFTP_KEY" | base64 --decode > ~/.ssh/id_rsa
chmod 400 ~/.ssh/id_rsa

REPO_DIR="${SFTP_DIR}/${TRAVIS_REPO_SLUG}"
VERSION_DIR="${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}"
BASE_DIR="$REPO_DIR/$VERSION_DIR"
NATIVES_DIR="$BASE_DIR/natives"
BASE_LOC="${SFTP_USER}@${SFTP_HOST}"
LATEST_LINK="$REPO_DIR/latest"

ssh -o "StrictHostKeyChecking no" "$BASE_LOC" "mkdir -p '$NATIVES_DIR' && rm -f '$LATEST_LINK' && ln -s '$VERSION_DIR' '$LATEST_LINK'"

mv "TachiServer/build/libs/$(ls TachiServer/build/libs | grep TachiServer-all)" /tmp/server.jar

rsync -v -e ssh /tmp/server.jar "$BASE_LOC:$BASE_DIR"

ls -1 bootui/tachiweb-bootstrap/dist | grep -i tachiweb | while read x; do
    if [[ ${x} == *.blockmap ]]; then
        continue
    fi

    BIN_PATH="$(realpath "bootui/tachiweb-bootstrap/dist/$x")"

    rsync -v -e ssh "$BIN_PATH" "$BASE_LOC:$NATIVES_DIR"
done

if [[ -d bootui/tachiweb-bootstrap/dist/squirrel-windows ]]; then
    ls -1 bootui/tachiweb-bootstrap/dist/squirrel-windows | grep -i tachiweb | while read x; do
        if [[ ${x} == *.blockmap ]]; then
            continue
        fi

        BIN_PATH="$(realpath "bootui/tachiweb-bootstrap/dist/squirrel-windows/$x")"

        rsync -v -e ssh "$BIN_PATH" "$BASE_LOC:$NATIVES_DIR"
    done
fi
