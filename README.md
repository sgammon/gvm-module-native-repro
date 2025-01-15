# graalvm bug repro

**Summary:** Building a Java entrypoint module and enabling that module for native access with `--enable-native-access` should work, but instead fails.

## Demonstration

Clone the code, assign the latest GraalVM release (Java 23 at the time of this writing) as your `JAVA_HOME`, then run `make`:

```
rm -fr ./target
rm -fr libs && mkdir -p libs
cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/nativeimage/24.1.1/nativeimage-24.1.1.jar
--2025-01-15 15:11:25--  https://repo1.maven.org/maven2/org/graalvm/sdk/nativeimage/24.1.1/nativeimage-24.1.1.jar
Resolving repo1.maven.org (repo1.maven.org)... 199.232.192.209, 199.232.196.209, 2a04:4e42:4c::209, ...
Connecting to repo1.maven.org (repo1.maven.org)|199.232.192.209|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 226628 (221K) [application/java-archive]
Saving to: ‘nativeimage-24.1.1.jar’

nativeimage-24.1.1.jar                                              100%[=================================================================================================================================================================>] 221.32K  --.-KB/s    in 0.04s

2025-01-15 15:11:25 (5.20 MB/s) - ‘nativeimage-24.1.1.jar’ saved [226628/226628]

cd libs && wget https://repo1.maven.org/maven2/org/graalvm/sdk/word/24.1.1/word-24.1.1.jar
--2025-01-15 15:11:25--  https://repo1.maven.org/maven2/org/graalvm/sdk/word/24.1.1/word-24.1.1.jar
Resolving repo1.maven.org (repo1.maven.org)... 199.232.192.209, 199.232.196.209, 2a04:4e42:4c::209, ...
Connecting to repo1.maven.org (repo1.maven.org)|199.232.192.209|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 22529 (22K) [application/java-archive]
Saving to: ‘word-24.1.1.jar’

word-24.1.1.jar                                                     100%[=================================================================================================================================================================>]  22.00K  --.-KB/s    in 0.003s

2025-01-15 15:11:25 (6.91 MB/s) - ‘word-24.1.1.jar’ saved [22529/22529]

mkdir -p target/classes
javac -d target/classes --module-path libs/nativeimage-24.1.1.jar:libs/word-24.1.1.jar Entry.java NativeFfmFeature.java module-info.java
cd target/classes && jar --create --file ../classes.jar --main-class example.Entry --module-version 1.0 example module-info.class
jar --file=target/classes.jar --describe-module
example.repro@1.0 jar:file:///home/sam/workspace/2am/labs/repro/gvm-module-entry/target/classes.jar!/module-info.class
requires java.base
requires org.graalvm.nativeimage static
requires org.graalvm.word static
contains example
main-class example.Entry

native-image --module-path target/classes -m example.repro/example.Entry --enable-native-access=example.repro --initialize-at-build-time= -o target/repro --features=example.NativeFfmFeature -H:+UnlockExperimentalVMOptions -H:+ForeignAPISupport
WARNING: Unknown module: example.repro specified to --enable-native-access
========================================================================================================================
GraalVM Native Image: Generating 'repro' (executable)...
========================================================================================================================
[1/8] Initializing...                                                                                    (2.5s @ 0.07GB)
 Java version: 23.0.1+11, vendor version: Oracle GraalVM 23.0.1+11.1
 Graal compiler: optimization level: 2, target machine: x86-64-v3, PGO: ML-inferred
 C compiler: gcc (linux, x86_64, 13.3.0)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
 2 user-specific feature(s):
 - com.oracle.svm.thirdparty.gson.GsonFeature
 - example.NativeFfmFeature
------------------------------------------------------------------------------------------------------------------------
 1 experimental option(s) unlocked:
 - '-H:+ForeignAPISupport' (origin(s): command line)
------------------------------------------------------------------------------------------------------------------------
Build resources:
 - 12.69GB of memory (42.1% of 30.17GB system memory, determined at start)
 - 32 thread(s) (100.0% of 32 available processor(s), determined at start)
[2/8] Performing analysis...  [*****]                                                                    (2.2s @ 0.30GB)
    2,596 reachable types   (63.4% of    4,097 total)
    2,429 reachable fields  (39.2% of    6,199 total)
   12,499 reachable methods (42.1% of   29,677 total)
      903 types,    17 fields, and   142 methods registered for reflection
       49 types,    34 fields, and    48 methods registered for JNI access
        1 downcalls and 0 upcalls registered for foreign access
        4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                               (0.6s @ 0.31GB)
[4/8] Parsing methods...      [*]                                                                        (0.8s @ 0.33GB)
[5/8] Inlining methods...     [***]                                                                      (0.3s @ 0.39GB)
[6/8] Compiling methods...    [***]                                                                      (5.7s @ 0.43GB)
[7/8] Laying out methods...   [*]                                                                        (0.7s @ 0.41GB)
[8/8] Creating image...       [*]                                                                        (0.6s @ 0.55GB)
   4.47MB (46.30%) for code area:     6,066 compilation units
   4.44MB (45.97%) for image heap:   65,171 objects and 52 resources
 763.89kB ( 7.73%) for other data
   9.65MB in total
------------------------------------------------------------------------------------------------------------------------
Top 10 origins of code area:                                Top 10 object types in image heap:
   2.61MB java.base                                            1.06MB byte[] for code metadata
   1.51MB svm.jar (Native Image)                             821.43kB byte[] for java.lang.String
  88.75kB com.oracle.svm.svm_enterprise                      480.83kB heap alignment
  36.97kB org.graalvm.nativeimage.base                       450.55kB java.lang.Class
  34.55kB jdk.proxy2                                         443.13kB java.lang.String
  33.42kB example.repro                                      138.70kB byte[] for reflection metadata
  28.39kB jdk.proxy1                                         138.69kB java.util.HashMap$Node
  27.34kB jdk.graal.compiler                                 121.69kB com.oracle.svm.core.hub.DynamicHubCompanion
  21.26kB org.graalvm.collections                            114.52kB char[]
  21.06kB jdk.internal.vm.ci                                  86.02kB java.lang.Object[]
  18.70kB for 5 more packages                                658.29kB for 687 more object types
                            Use '--emit build-report' to create a report with more details.
------------------------------------------------------------------------------------------------------------------------
Security report:
 - Binary includes Java deserialization.
 - Use '--enable-sbom' to assemble a Software Bill of Materials (SBOM).
------------------------------------------------------------------------------------------------------------------------
Recommendations:
 G1GC: Use the G1 GC ('--gc=G1') for improved latency and throughput.
 PGO:  Use Profile-Guided Optimizations ('--pgo') for improved throughput.
 HEAP: Set max heap for improved and more predictable memory usage.
 CPU:  Enable more CPU features with '-march=native' for improved performance.
 QBM:  Use the quick build mode ('-Ob') to speed up builds during development.
------------------------------------------------------------------------------------------------------------------------
                       1.0s (6.7% of total time) in 212 GCs | Peak RSS: 1.44GB | CPU load: 16.02
------------------------------------------------------------------------------------------------------------------------
Build artifacts:
 /home/sam/workspace/2am/labs/repro/gvm-module-entry/target/repro (executable)
========================================================================================================================
Finished generating 'repro' in 14.0s.
java --module-path target/classes.jar --enable-native-access=example.repro -m example.repro/example.Entry
Repro start
*
**
***
****
*****
******
*******
********
Repro finished
./target/repro
Repro start
Exception in thread "main" java.lang.IllegalCallerException: Illegal native access from: module example.repro
        at java.base@23.0.1/java.lang.Module.ensureNativeAccess(Module.java:312)
        at java.base@23.0.1/java.lang.System$2.ensureNativeAccess(System.java:2543)
        at java.base@23.0.1/jdk.internal.reflect.Reflection.ensureNativeAccess(Reflection.java:122)
        at java.base@23.0.1/jdk.internal.foreign.layout.ValueLayouts$OfAddressImpl.withTargetLayout(ValueLayouts.java:335)
        at example.repro/example.Entry.invokeStrdup(Entry.java:31)
        at example.repro/example.Entry.main(Entry.java:10)
        at java.base@23.0.1/java.lang.invoke.LambdaForm$DMH/sa346b79c.invokeStaticInit(LambdaForm$DMH)
make: *** [Makefile:24: test] Error 1
```

Instead of an identical printed sample, an exception is thrown. Despite being enabled via `--enable-native-access`, the entrypoint module does not have native access.

Perhaps relatedly, invoking `native-image` with `-m example.repro` (module, but no class name) reveals:

```
WARNING: Unknown module: example.repro specified to --enable-native-access
========================================================================================================================
GraalVM Native Image: Generating 'repro' (executable)...
========================================================================================================================
[1/8] Initializing...                                                                                    (0.0s @ 0.15GB)
Error: Module example.repro does not have a ModuleMainClass attribute, use -m <module>/<main-class>
make: *** [Makefile:18: target/repro] Error 1
```

The output of `jar --file=... --describe-module`:

```
➜  gvm-module-entry git:(master) ✗ jar --file=./target/classes.jar --describe-module
example.repro@1.0 jar:file:///.../gvm-module-entry/./target/classes.jar!/module-info.class
requires java.base
requires org.graalvm.nativeimage static
requires org.graalvm.word static
contains example
main-class example.Entry
```

