import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Reproduces the real, classic application-server "redeploy leak": a
// ThreadLocal set by a webapp-loaded object, on a container-managed pooled
// thread that outlives the webapp's own redeploy, keeps the webapp's entire
// classloader -- and therefore every class it loaded -- alive forever.
public class ClassloaderLeakDemo {

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);

        System.out.println("=== \"Deploying\" a webapp: loading PluginTask via its OWN, isolated classloader ===");
        Path classesDir = Path.of("out");
        IsolatedClassLoader webappClassLoader = new IsolatedClassLoader(classesDir, ClassloaderLeakDemo.class.getClassLoader());
        Class<?> pluginClass = webappClassLoader.loadClass("PluginTask");
        Runnable pluginTask = (Runnable) pluginClass.getDeclaredConstructor().newInstance();
        System.out.println("Real defining classloader for PluginTask: " + pluginClass.getClassLoader());

        WeakReference<ClassLoader> classLoaderRef = new WeakReference<>(webappClassLoader);

        System.out.println();
        System.out.println("=== Running the plugin task on a real, long-lived pooled thread ===");
        pool.submit(pluginTask).get();
        System.out.println("Real ThreadLocal.set() called on the pooled thread -- no remove() anywhere.");

        System.out.println();
        System.out.println("=== \"Undeploying\" the webapp: dropping every direct reference ===");
        webappClassLoader = null;
        pluginTask = null;
        pluginClass = null;

        System.out.println();
        System.out.println("=== BUGGY: forcing GC -- is the classloader actually collected? ===");
        forceGc();
        System.out.println("Real classloader still reachable after GC: " + (classLoaderRef.get() != null)
                + " (leaked -- the pooled thread's ThreadLocal entry still references a PluginTask instance)");

        System.out.println();
        System.out.println("=== FIXED: calling ThreadLocal.remove() on the SAME pooled thread ===");
        pool.submit(() -> LeakyThreadLocalHolder.HOLDER.remove()).get();
        forceGc();
        System.out.println("Real classloader still reachable after GC: " + (classLoaderRef.get() != null)
                + " (now correctly collected)");

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private static void forceGc() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }
}
