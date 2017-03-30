# TachiWeb-UI
TachiWeb-UI is the web frontend for TachiServer.

# About
*TachiWeb-UI is a component of TachiWeb, an attempt to port [Tachiyomi](https://github.com/inorichi/tachiyomi) to the desktop.*

TachiWeb-UI is a UI for TachiServer which attempts to implement as much of the functionality present in Tachiyomi as possible.
It currently copies almost every element of it's design from Tachiyomi, optimizations to the desktop platform will be made later.

TachiWeb is not officially supported by Tachiyomi or it's contributors.

# Live demo
A live demo is available here: [http://d3.nulldev.xyz:4567/](http://d3.nulldev.xyz:4567/)
**NOTE**: Kissmanga is very unstable. Batoto normally requires log in but in the demo, I will use my own credentials.
Please don't destroy the live demo.

# Features
- Almost the entire Tachiyomi UI has been implemented!
- Simple RTL and LTR paging reader implemented.
- Full support for chapter downloading and download management.
- Add new manga from the catalogue (essentially fully implemented).
- Backup/export and restore/import your library to and from Tachiyomi!
- Run the server software on any server for remote access to your library (read manga anywhere with a internet connection!)
- Authentication

# Planned Features
- Persistent library filters
- Category editing
- Multi-select in all UIs
- Display and sorting mode customization in chapters UI
- Better cross-tab/window syncing
- Seamless sync with Tachiyomi

# Installation
See the [TachiWeb-Server README](https://github.com/TachiWeb/TachiWeb-Server#installation).

# Architecture
The Javascript code is not very neat and currently needs a lot of refactoring, although I know how to write Javascript, I am not well-acquainted with it's best practices and design patterns.

# State
TachiWeb-UI is usable, but still **alpha** software.
There are probably still many bugs present, if you find one, please report it by creating an issue.
Performance is currently very good and the UI is quite fluid.
Many buttons/elements of the UI still do nothing and are unimplemented, they will implemented eventually but usually higher priority features are implemented first.

# License
```
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
```
