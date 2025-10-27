default:
	./gradlew clean check

install:
	./gradlew clean check
	brew uninstall bashpile || true
	cp build/native/nativeCompile/bashpile /usr/local/bin/bashpile

# used by Homebrew Formula
jar:
    export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
	./gradlew clean build --info --stacktrace -x test -x integrationTest -x nativeCompile

