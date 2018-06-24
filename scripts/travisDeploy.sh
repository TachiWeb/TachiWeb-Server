#!/usr/bin/env bash

mkdir ~/.ssh
echo "$SFTP_KEY" | base64 --decode > ~/.ssh/id_rsa

BASE_DIR="${SFTP_USER}@${SFTP_HOST}${SFTP_DIR}/${TRAVIS_REPO_SLUG}/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}"

mv "$(ls TachiServer/build/libs | grep TachiServer-all)" /tmp/server.jar

rsync -v -e ssh /tmp/server.jar "$BASE_DIR"

ls -1 bootui/tachiweb-bootstrap/dist | grep -i tachiweb* | while read x; do
    BIN_PATH="$(realpath "bootui/tachiweb-bootstrap/dist/$x")"

    rsync -v -e ssh "$BIN_PATH" "$BASE_DIR/natives"
done
