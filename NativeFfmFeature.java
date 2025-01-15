package example;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import static java.lang.foreign.ValueLayout.*;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.Feature.DuringSetupAccess;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

class NativeFfmFeature implements Feature { 
  public void duringSetup(DuringSetupAccess access) {
    RuntimeForeignAccess.registerForDowncall(FunctionDescriptor.ofVoid());
    RuntimeForeignAccess.registerForUpcall(FunctionDescriptor.ofVoid());
    RuntimeForeignAccess.registerForDowncall(FunctionDescriptor.ofVoid(), Linker.Option.critical(false));
    RuntimeForeignAccess.registerForUpcall(FunctionDescriptor.of(JAVA_INT, JAVA_INT));
    RuntimeForeignAccess.registerForUpcall(FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT));
    RuntimeForeignAccess.registerForDowncall(FunctionDescriptor.of(ADDRESS, JAVA_INT, JAVA_INT), Linker.Option.firstVariadicArg(1));
    RuntimeForeignAccess.registerForDowncall(FunctionDescriptor.ofVoid(JAVA_INT), Linker.Option.captureCallState("errno"));
  }
}
