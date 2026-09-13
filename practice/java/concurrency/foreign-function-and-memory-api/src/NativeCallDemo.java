import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

// Calls a REAL native C library function (libc's strlen) directly from pure
// Java -- zero JNI glue code, no native compilation step, no .so/.dylib of
// our own. This is the actual replacement for JNI the FFM API provides.
public class NativeCallDemo {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();
        MethodHandle strlen = linker.downcallHandle(
                linker.defaultLookup().find("strlen").orElseThrow(
                        () -> new IllegalStateException("native strlen symbol not found")),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

        try (Arena arena = Arena.ofConfined()) {
            String javaString = "Hello from pure Java, calling real native libc code with zero JNI!";
            MemorySegment nativeString = arena.allocateUtf8String(javaString);

            long realNativeLength = (long) strlen.invoke(nativeString);

            System.out.println("Java string:              \"" + javaString + "\"");
            System.out.println("Real Java String.length(): " + javaString.length());
            System.out.println("Real native strlen() result: " + realNativeLength);
            System.out.println("Match: " + (realNativeLength == javaString.length()));
        }
    }
}
