#!/usr/bin/env bash
git submodule foreach git reset --hard
git submodule update --init --remote
git submodule foreach "git checkout master; git pull"
