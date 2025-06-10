# Contributing

## Quick Start
### Setup env
Any script setup (e.g., for SDKMAN) should be reachable from .profile 
(e.g., have it source .bashrc or .bash_profile if needed).

Ensure your IDE can display Mermaid syntax.

### Build
`./gradlew clean check`
Native executable is generated to bin/bashpile

## Design

1. Parse arguments with [CLIKT](https://ajalt.github.io/clikt/).  
2. Parse Bashpile script with Antlr to Antlr AST.
3. Transform into Bashpile AST.  
4. Render Bashpile AST as Bash script.