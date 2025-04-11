# Contributing

## Quick Start
### Setup env
Any script setup (e.g. for SDKMAN) should be reachable from .profile 
(e.g. have it source .bashrc or .bash_profile if needed).
### Build
`./gradlew clean build`

## Design

Parse arguments with [CLIKT](https://ajalt.github.io/clikt/).  Compile script with Antlr.  Render Bash script.
