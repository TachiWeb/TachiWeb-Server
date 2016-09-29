# TachiWeb-Server
TachiWeb-Server is the server component of TachiWeb.

# About
*TachiWeb-Server is a component of TachiWeb, an attempt to port [Tachiyomi](https://github.com/inorichi/tachiyomi) to the desktop.*

TachiWeb-Server provides a consistent and flexible API for programs using the Tachiyomi backend.
TachiWeb-Server allows multiple programs to use the same Tachiyomi library at the same time and provides a foundation for alternative frontends to build on.

TachiWeb is not officially supported by Tachiyomi or it's contributors.

# Live demo
A live demo is available here: [http://64.137.238.70:4567/](http://64.137.238.70:4567/)
**NOTE**: Kissmanga is very unstable. Batoto normally requires log in but in the demo, I will use my own credentials.
The demo server is also very cheap so don't expect images to load very fast.
Please don't destroy the live demo.

# Motivation
There is currently no Manga reader that can be easily synced between the desktop and mobile.
TachiWeb-Server is full compatible with Tachiyomi's data structures and backups, allowing easy migration between the two.
**Seamless sync between the two programs is a goal and planned feature.**

# Installation
Make sure you have `maven` and `bower` installed before starting.

1. Clone the repository by executing: `git clone https://github.com/TachiWeb/TachiWeb-Server`
2. Enter the repository folder by executing: `cd TachiWeb-Server`
3. Build TachiWeb-Server and TachiWeb-UI by executing: `./build.sh`
4. Now you can launch TachiWeb by running `cd target && java -jar TachiServer-[Whatever version]-jar-with-dependencies.jar`. Open the UI by going to: [http://localhost:4567/](http://localhost:4567/) in a web browser.

# Architecture
TachiWeb-Server shares a large portion of it's backend with Tachiyomi.
In fact, almost the entire backend was copied directly from Tachiyomi without any modification.
To make up for the lack of Android classes, a no-op Android JAR is used and any missing features are reimplemented/copied from Android.

# State
TachiWeb-Server usable, but still **alpha** software.
Do **NOT** use it as your primary manga reader *yet*.
Although a lot of effort has gone into stability, TachiWeb-Server may still periodically crash and/or corrupt library data.

# License
Copyright 2016 Andy Bao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.