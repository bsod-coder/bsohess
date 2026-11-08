# Bso Chess (work in progress)

This is an unfinished Android project, not a playable bot or analysis app. The current prototype provides a local two-player board powered by chesslib. The engine, analysis, import, and coaching features have not been implemented.

The GitHub Actions workflow builds and runs unit tests with JDK 17 and Android SDK. For local builds install SDK Platform 37 and run `./gradlew assembleDebug testDebugUnitTest` in this directory.

Stockfish is not yet bundled. Once integrated, the distributed application must comply with the GNU GPL version 3, including its corresponding-source and notice requirements; the existing repository MIT license alone will not satisfy those obligations.
