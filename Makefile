default:
	./gradlew clean check

install:
	./gradlew clean check
	brew uninstall bashpile || true
	cp build/native/nativeCompile/bashpile /usr/local/bin/bashpile

# used by Homebrew Formula
jar:
	./gradlew clean build --debug --stacktrace -x test -x integrationTest -x nativeCompile
