package example.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Util {
  public static void invokeStrdup(String pattern) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {

            // Allocate off-heap memory and
            // copy the argument, a Java string, into off-heap memory
            MemorySegment nativeString = arena.allocateFrom(pattern);

            // Obtain an instance of the native linker
            Linker linker = Linker.nativeLinker();

            // Locate the address of the C function signature
            SymbolLookup stdLib = linker.defaultLookup();
            MemorySegment strdup_addr = stdLib.find("strdup").get();

            // Create a description of the C function
            var layout = MemoryLayout.sequenceLayout(Long.MAX_VALUE, ValueLayout.JAVA_BYTE);
            FunctionDescriptor strdup_sig = FunctionDescriptor.of(
                    ValueLayout.ADDRESS.withTargetLayout(layout),
                    ValueLayout.ADDRESS.withTargetLayout(layout)
            );

            // Create a downcall handle for the C function
            MethodHandle strdup_handle = linker.downcallHandle(strdup_addr, strdup_sig);

            // Call the C function directly from Java
            MemorySegment duplicatedAddress = (MemorySegment) strdup_handle.invokeExact(nativeString);

            for (int i = pattern.length() - 1; i >= 0; i--) {
                System.out.println(duplicatedAddress.getString(i));
            }
        }
  }
}

