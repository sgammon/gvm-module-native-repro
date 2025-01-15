package example.entry;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Entry {
  public static void main(String[] args) throws Throwable {
     System.out.println("Repro start");
     String pattern = (args.length > 0) ? args[0] : "********";
     example.ffm.Util.invokeStrdup(pattern);
     System.out.println("Repro finished");
  }
}

