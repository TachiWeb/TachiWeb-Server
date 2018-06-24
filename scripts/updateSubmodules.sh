#!/usr/bin/env bash
git submodule foreach git reset --hard
git submodule foreach "git pull"
