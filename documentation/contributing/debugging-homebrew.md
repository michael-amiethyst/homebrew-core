# Debugging Homebrew

This is for when you can run `make jar` locally but the `bin/install` fails.

To install with debugging info, run
`HOMEBREW_NO_INSTALL_FROM_API=1 brew install --build-from-source --verbose --debug --HEAD bashpile`.
This should build from scratch using HEAD (development branch).