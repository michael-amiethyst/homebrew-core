# Quickstart

1. `./gradlew clean check` for all integration tests and native compile
   1. Ensure Java 21 by GraalVM is your JVM
   2. You may want to use [sdkman](https://sdkman.io/) (with sdkman_auto_env enabled)
      1. A .sdkman file is included here
   3. Ensure any needed setup (e.g. for sdkman) is reachable from sourcing your `~/.profile`
2. or `./gradlew clean build` for a quicker build
   1. Also check for JVM and ~/.profile setup as above

# More Docs

See [documentation directory](documentation/README.md)
