all: clean target/classes target/repro jtest test

clean:
	rm -fr ./target

target/classes:
	mkdir -p target/classes
	javac -d target/classes Entry.java module-info.java
	cd target/classes && jar --create --file ../classes.jar --main-class example.Entry --module-version 1.0 example module-info.class
	jar --file=target/classes.jar --describe-module

target/repro:
	native-image --module-path target/classes.jar -m example.repro --enable-native-access=example.repro -o target/repro -H:+UnlockExperimentalVMOptions -H:+ForeignAPISupport

jtest:
	java --module-path target/classes.jar --enable-native-access=example.repro -m example.repro/example.Entry

test:
	./target/repro

