## Installing from binary (recommended)
1. Download the latest binary from here: `https://ci.nulldev.xyz/job/TachiWeb-Server/lastSuccessfulBuild/artifact/TachiServer/build/libs/TachiServer-all-1.0.jar`
2. Run the JAR file: `java -jar TachiServer-all-1.0.jar`.
3. Access TachiWeb in your browser here: http://127.0.0.1:4567/.

## Compiling from source

### Requirements

**OS -** TachiWeb can be built and compiled on most Linux and Mac systems.

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
- mvn (Maven)

### Building
1. Download the source code:
```
git clone https://github.com/TachiWeb/TachiWeb-Server
```
2. Build the project (will take a long time):
```
scripts/build.sh
```
**NOTE:** You may encounter many "zip errors" when building. These errors can be safely ignored.

Once the build completes, the binary can be found at: `TachiServer/build/libs/TachiServer-all-$VERSION.jar`

3. You can now try running the program:
```
cd TachiServer/build/libs
java -jar TachiServer-all-*.jar
```
The web server runs on port `4567`. Access it in your browser here: http://127.0.0.1:4567/.
