all: clean libs target/classes.jar target/repro jtest test

clean:
	rm -fr ./target

libs:
	rm -fr libs && mkdir -p libs
	cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/nativeimage/24.1.1/nativeimage-24.1.1.jar
	cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/word/24.1.1/word-24.1.1.jar

target/util.jar:
	mkdir -p target/classes
	javac --enable-preview --release 23 -d target/classes --module-path libs/nativeimage-24.1.1.jar:libs/word-24.1.1.jar util/Util.java util/NativeFfmFeature.java util/module-info.java
	cd target/classes && jar --create --file ../util.jar example module-info.class
	jar --file=target/util.jar --describe-module

target/classes.jar: target/util.jar
	rm -fr target/classes
	mkdir -p target/classes
	javac --enable-preview --release 23 -d target/classes --module-path target/util.jar:libs/nativeimage-24.1.1.jar:libs/word-24.1.1.jar entry/Entry.java entry/module-info.java
	cd target/classes && jar --create --file ../classes.jar --main-class example.entry.Entry example module-info.class
	jar --file=target/classes.jar --describe-module

target/repro:
	native-image --enable-preview --module-path target/classes.jar:target/util.jar -m example.repro/example.entry.Entry --enable-native-access=example.ffm --initialize-at-build-time= -o target/repro --features=example.ffm.NativeFfmFeature -H:+UnlockExperimentalVMOptions -H:+ForeignAPISupport

jtest:
	java --module-path target/classes.jar:target/util.jar --enable-native-access=example.ffm -m example.repro/example.entry.Entry

test:
	./target/repro

.PHONY: all clean libs jtest test

