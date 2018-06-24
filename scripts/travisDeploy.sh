#!/usr/bin/env bash

mkdir -p ~/.ssh
echo "$SFTP_KEY" | base64 --decode > ~/.ssh/id_rsa
chmod 400 ~/.ssh/id_rsa

BASE_DIR="${SFTP_DIR}/${TRAVIS_REPO_SLUG}/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}"
NATIVES_DIR="$BASE_DIR/natives"
BASE_LOC="${SFTP_USER}@${SFTP_HOST}"

ssh -o "StrictHostKeyChecking no" "$BASE_LOC" "mkdir -p '$NATIVES_DIR'"

mv "$(ls TachiServer/build/libs | grep TachiServer-all)" /tmp/server.jar

rsync -v -e ssh /tmp/server.jar "$BASE_LOC$BASE_DIR"

ls -1 bootui/tachiweb-bootstrap/dist | grep -i tachiweb* | while read x; do
    BIN_PATH="$(realpath "bootui/tachiweb-bootstrap/dist/$x")"

    rsync -v -e ssh "$BIN_PATH" "$BASE_LOC$NATIVES_DIR"
done
