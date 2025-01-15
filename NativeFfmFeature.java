package example;

import java.lang.foreign.FunctionDescriptor;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.Feature.DuringSetupAccess;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

class NativeFfmFeature implements Feature {
  @Override
  public void duringSetup(DuringSetupAccess access) {
    RuntimeForeignAccess.registerForDowncall(
      FunctionDescriptor.ofVoid()
    );
  }
}
