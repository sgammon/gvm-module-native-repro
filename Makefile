all: clean libs target/classes target/repro jtest test

clean:
	rm -fr ./target

libs:
	rm -fr libs && mkdir -p libs
	cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/nativeimage/24.1.1/nativeimage-24.1.1.jar
	cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/word/24.1.1/word-24.1.1.jar

target/classes:
	mkdir -p target/classes
	javac -d target/classes --module-path libs/nativeimage-24.1.1.jar:libs/word-24.1.1.jar Entry.java NativeFfmFeature.java module-info.java
	cd target/classes && jar --create --file ../classes.jar --main-class example.Entry --module-version 1.0 example module-info.class
	jar --file=target/classes.jar --describe-module

target/repro:
	native-image --module-path target/classes -m example.repro --enable-native-access=example.repro --initialize-at-build-time= -o target/repro --features=example.NativeFfmFeature -H:+UnlockExperimentalVMOptions -H:+ForeignAPISupport

jtest:
	java --module-path target/classes.jar --enable-native-access=example.repro -m example.repro/example.Entry

test:
	./target/repro

.PHONY: all clean libs jtest test

