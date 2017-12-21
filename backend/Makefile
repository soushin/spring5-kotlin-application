build: compile

test:
	./gradlew clean generateProto test

compile:
	./gradlew clean generateProto compileKotlin compileJava

doc:
	apidoc -i src/test -i docs/define -o docs/artifact
