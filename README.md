# TachiWeb-Server [![Travis status](https://api.travis-ci.org/TachiWeb/TachiWeb-Server.svg?branch=develop)](https://travis-ci.org/TachiWeb/TachiWeb-Server)
TachiWeb-Server is a port of [Tachiyomi](https://github.com/inorichi/tachiyomi) to the desktop and server platforms.

## Downloads
[![Windows installer download](.github/Windows.png)](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-win-Setup.exe)&nbsp;&nbsp;
[![Linux 64-bit AppImage download](.github/Linux.png)](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-x86_64.AppImage)&nbsp;&nbsp;
[![Mac DMG download](.github/macOS.png)](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-mac.dmg)

<details>
  <summary>Show all downloads</summary>
  <p>
  
  | Windows | Linux | macOS | Server-only |
  | --- | --- | --- | --- |
  | [64-bit/32-bit installation file](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-win-Setup.exe) | [32-bit AppImage](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-i386.AppImage) | [DMG image](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-mac.dmg) | [Executable JAR](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/server.jar) |
  | [32-bit portable zip archive](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-win32-portable.zip) | [64-bit AppImage](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-x86_64.AppImage) | [zip archive](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-mac.zip) |
  | [64-bit portable zip archive](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-win64-portable.zip) | [64-bit .pacman package](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-x64.pacman) | |
  |  | [32-bit .tar.gz archive](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-ia32.tar.gz) | |
  |  | [64-bit .tar.gz archive](https://travis.nd.ax/TachiWeb/TachiWeb-Server/latest/natives/TachiWeb-linux-x64.tar.gz) | |
  
  [Older builds](https://travis.nd.ax/TachiWeb/TachiWeb-Server/)
  
  </p>
</details>

<br>

**Java 8 or newer is required to run the application.**

## About
TachiWeb-Server provides a consistent and flexible API for programs using the Tachiyomi backend.
TachiWeb-Server allows multiple programs to use the same Tachiyomi library at the same time and provides a foundation for alternative frontends to build on.

Currently, this project comes bundled with a React frontend that is enabled by default and a bootstrapper to integrate the application with your desktop.

TachiWeb is not officially supported by Tachiyomi or it's contributors.

## Motivation
There is currently no Manga reader that can be easily synced between the desktop and mobile.
TachiWeb-Server is fully compatible with Tachiyomi's data structures and backups, allowing easy migration between the two.
**Seamless sync between the two programs is a goal and planned feature.**

## Server setup
TachiWeb can also be installed on servers. Refer to [INSTALL.md](https://github.com/TachiWeb/TachiWeb-Server/blob/master/INSTALL.md) for the server installation procedure.

# State
TachiWeb-Server usable, but still **alpha** software.
The API is not stable and can change at any time.
Details on the state of the project can be found here: [https://github.com/TachiWeb/TachiWeb-Server/projects](https://github.com/TachiWeb/TachiWeb-Server/projects).

# License
```
Copyright 2019 Andy Bao and contributors

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
