## Build instructions

### Requirements

**OS -** TachiWeb currently only compiles properly on Linux.

**Tools -** The following programs must be installed (and meet version requirements) to build this project:
- bower >= 1.8.0
- npm >= 3.0.0
- curl
- zip
- unzip
- bash
- java >= 8
- grep
- realpath

### Building
1. Download the source code:
```
git clone https://github.com/null-dev/TW-Compat
```
2. Build the project (will take a long time):
```
scripts/build.sh
```
Once the build completes, the binary can be found at: `TachiServer/build/libs/TachiServer-all-$VERSION.jar`

3. You can now try running the program:
```
cd TachiServer/build/libs
java -jar TachiServer-all-*.jar
```
The web server runs on port `4567`. Access it in your browser here: http://127.0.0.1:4567/.
