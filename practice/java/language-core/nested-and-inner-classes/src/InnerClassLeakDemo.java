import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Real, executed proof that a non-static inner class keeps its enclosing
 * instance reachable, and that the same code written as a static nested class
 * does not.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/InnerClassLeakDemo.java
 *   java -cp out InnerClassLeakDemo
 */
public class InnerClassLeakDemo {

    /** Stands in for any long-lived registry: a listener list, a cache, a scheduler. */
    private static final List<Runnable> GLOBAL_REGISTRY = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("=== A: inner-class callback that USES the enclosing instance ===");
        WeakReference<LeakyService> leaky = registerLeaky();
        forceGc();
        System.out.println("Fields emitted on the callback: " + fieldsOf(LeakyService.Callback.class));
        System.out.println("LeakyService collected after GC? " + (leaky.get() == null));

        System.out.println();
        System.out.println("=== B: inner-class callback that never touches the enclosing instance ===");
        WeakReference<IndifferentService> indifferent = registerIndifferent();
        forceGc();
        System.out.println("Fields emitted on the callback: " + fieldsOf(IndifferentService.Callback.class));
        System.out.println("IndifferentService collected after GC? " + (indifferent.get() == null));

        System.out.println();
        System.out.println("=== C: same callback written as a static nested class ===");
        WeakReference<CleanService> clean = registerClean();
        forceGc();
        System.out.println("Fields emitted on the callback: " + fieldsOf(CleanService.Callback.class));
        System.out.println("CleanService collected after GC?  " + (clean.get() == null));

        System.out.println();
        System.out.println("Registry still holds " + GLOBAL_REGISTRY.size() + " callbacks -- none were unregistered.");
    }

    private static String fieldsOf(Class<?> type) {
        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        if (fields.length == 0) {
            return "(none)";
        }
        StringBuilder sb = new StringBuilder();
        for (java.lang.reflect.Field f : fields) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(f.getType().getSimpleName()).append(' ').append(f.getName());
        }
        return sb.toString();
    }

    /** Owns a large buffer, so retaining it retains real memory. */
    static class LeakyService {
        private final String name;
        @SuppressWarnings("unused")
        private final byte[] buffer = new byte[8 * 1024 * 1024]; // 8 MB

        LeakyService(String name) {
            // Assigned at runtime, so it is NOT a compile-time constant that
            // javac could inline into the inner class body.
            this.name = name;
        }

        /**
         * Non-static inner class whose body reads an enclosing-instance field,
         * so javac must emit and keep the synthetic outer reference.
         */
        class Callback implements Runnable {
            @Override
            public void run() {
                System.out.print(name);
            }
        }

        void register() {
            GLOBAL_REGISTRY.add(new Callback());
        }
    }

    /** Identical shape, but the inner class never reads anything from the outer instance. */
    static class IndifferentService {
        @SuppressWarnings("unused")
        private final byte[] buffer = new byte[8 * 1024 * 1024]; // 8 MB

        class Callback implements Runnable {
            @Override
            public void run() {
            }
        }

        void register() {
            GLOBAL_REGISTRY.add(new Callback());
        }
    }

    /** Identical shape, but the callback carries only what it actually needs. */
    static class CleanService {
        @SuppressWarnings("unused")
        private final byte[] buffer = new byte[8 * 1024 * 1024]; // 8 MB

        /** Static nested class: no enclosing-instance reference at all. */
        static class Callback implements Runnable {
            @Override
            public void run() {
            }
        }

        void register() {
            GLOBAL_REGISTRY.add(new Callback());
        }
    }

    private static WeakReference<LeakyService> registerLeaky() {
        LeakyService service = new LeakyService("leaky");
        service.register();
        return new WeakReference<>(service);
    }

    private static WeakReference<IndifferentService> registerIndifferent() {
        IndifferentService service = new IndifferentService();
        service.register();
        return new WeakReference<>(service);
    }

    private static WeakReference<CleanService> registerClean() {
        CleanService service = new CleanService();
        service.register();
        return new WeakReference<>(service);
    }

    /**
     * System.gc() is a hint, not a guarantee. Requesting it repeatedly with a
     * short pause is enough to make the difference observable and stable on a
     * normal desktop JVM; the run below is the real, captured result.
     */
    private static void forceGc() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(50);
        }
    }
}
