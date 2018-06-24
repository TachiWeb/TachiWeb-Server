#!/usr/bin/env bash
mkdir /tmp/curl
pushd /tmp/curl
wget "https://github.com/curl/curl/releases/download/curl-7_60_0/curl-7.60.0.tar.gz" -O curl.tar.gz
tar -xvf curl.tar.gz
mv curl-* build
pushd build
sudo apt install libssh2-1-dev libssl-dev
./configure --with-ssl --with-libssh2
make
sudo make install
popd
popd