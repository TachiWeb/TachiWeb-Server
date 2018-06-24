#!/usr/bin/env bash

# Mac OS curl hack
export PATH=/usr/local/bin:$PATH

curl --ftp-create-dirs \
    -T "$(ls TachiServer/build/libs | grep TachiServer-all)" \
    "sftp://${SFTP_USER}:${SFTP_PASSWORD}@${SFTP_HOST}${SFTP_DIR}/${TRAVIS_REPO_SLUG}/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}/server.jar"

ls bootui/tachiweb-bootstrap/dist -1 | grep -i tachiweb* | while read x; do
    BIN_PATH="$(realpath "bootui/tachiweb-bootstrap/dist/$x")"

    curl --ftp-create-dirs \
        -T "$BIN_PATH" \
        "sftp://${SFTP_USER}:${SFTP_PASSWORD}@${SFTP_HOST}${SFTP_DIR}/${TRAVIS_REPO_SLUG}/${TRAVIS_BUILD_NUMBER}_${TRAVIS_COMMIT}/natives/$x"
done
