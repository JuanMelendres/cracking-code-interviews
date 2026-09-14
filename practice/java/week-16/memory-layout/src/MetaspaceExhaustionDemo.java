import java.lang.reflect.*;
import java.util.*;

/**
 * Proves metaspace is a genuinely SEPARATE memory region from the heap by
 * exhausting it deliberately while the heap stays essentially untouched.
 *
 * Each iteration generates a brand-new, distinct class at runtime (a dynamic
 * proxy implementing a marker interface, which the JVM must load and give
 * its own class metadata) and keeps a strong reference to its Class object
 * so it's never unloaded. Class metadata lives in metaspace, not the heap --
 * this is what produces a real `OutOfMemoryError: Metaspace` (not a heap OOM)
 * once -XX:MaxMetaspaceSize is exhausted, with heap occupancy still low.
 *
 * Run with:
 *   java -Xmx512m -XX:MaxMetaspaceSize=32m -cp out MetaspaceExhaustionDemo
 */
public class MetaspaceExhaustionDemo {

    public interface Marker { }

    public static void main(String[] args) {
        List<Class<?>> generatedClasses = new ArrayList<>();
        int count = 0;
        try {
            while (true) {
                // Each ClassLoader here is fresh, and the proxy's generated
                // implementation class is genuinely distinct metadata every
                // time -- the JVM cannot dedupe or reuse it.
                ClassLoader freshLoader = new ClassLoader(MetaspaceExhaustionDemo.class.getClassLoader()) { };
                Object proxy = Proxy.newProxyInstance(
                        freshLoader,
                        new Class<?>[]{Marker.class},
                        (p, method, methodArgs) -> null
                );
                generatedClasses.add(proxy.getClass()); // strong reference: never unloaded
                count++;
                if (count % 2000 == 0) {
                    Runtime rt = Runtime.getRuntime();
                    System.out.printf("generated %d classes | heap used ~%dMB / %dMB%n",
                            count, (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024),
                            rt.maxMemory() / (1024 * 1024));
                }
            }
        } catch (OutOfMemoryError e) {
            // Deliberately avoid string concatenation / printf here: those compile to
            // invokedynamic call sites that themselves need fresh metaspace to link on
            // first use, and metaspace is exactly what's exhausted right now. Plain
            // println of already-resolved literals and String.valueOf (a regular
            // static method call, not invokedynamic) survive an OOM that a "+" or
            // printf call site freshly needed at this exact moment would not.
            Runtime rt = Runtime.getRuntime();
            System.out.println();
            System.out.println("CAUGHT java.lang.OutOfMemoryError, message:");
            System.out.println(e.getMessage());
            System.out.println("Total classes generated before OOM:");
            System.out.println(String.valueOf(count));
            System.out.println("Heap used at OOM (MB):");
            System.out.println(String.valueOf((rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)));
            System.out.println("Heap max (MB):");
            System.out.println(String.valueOf(rt.maxMemory() / (1024 * 1024)));
        }
    }
}
