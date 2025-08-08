default:
	./gradlew clean check

install:
	./gradlew clean check
	brew uninstall bashpile || true
	cp build/native/nativeCompile/bashpile /usr/local/bin/bashpile

jar:
	gradle clean build -x test -x integrationTest -x nativeCompile