import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Real, executed demonstration of Java's four nested-class forms and the
 * behavioral differences an interview actually asks about.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/NestedClassesDemo.java
 *   java -cp out NestedClassesDemo
 */
public class NestedClassesDemo {

    public static void main(String[] args) throws Exception {
        section("1. Construction: a static nested class needs no outer instance, an inner class does");
        constructionRules();

        section("2. The hidden field: what the compiler actually adds to an inner class");
        hiddenOuterReference();

        section("3. Outer.this: disambiguating a shadowed field");
        shadowedField();

        section("4. Serialization: an inner class drags its outer instance into the byte stream");
        serializationHazard();

        section("5. Local and anonymous classes capture effectively-final locals");
        localAndAnonymous();

        section("6. What the compiler emitted for each form");
        emittedClassNames();
    }

    // ---------------------------------------------------------------- 1

    /** Static nested: a top-level class that happens to live inside another namespace. */
    static class Config {
        private final int retries;

        Config(int retries) {
            this.retries = retries;
        }

        int retries() {
            return retries;
        }
    }

    /** Inner (non-static nested): every instance is bound to an enclosing instance. */
    class Session {
        String describe() {
            // Reads the ENCLOSING instance's field with no qualification at all.
            return "session of " + owner;
        }
    }

    private final String owner;

    public NestedClassesDemo() {
        this("default-owner");
    }

    public NestedClassesDemo(String owner) {
        this.owner = owner;
    }

    private static void constructionRules() {
        Config config = new Config(3);
        System.out.println("Static nested, no outer instance needed: new Config(3).retries() = " + config.retries());

        NestedClassesDemo outer = new NestedClassesDemo("alice");
        Session session = outer.new Session();
        System.out.println("Inner, requires an outer instance (outer.new Session()): " + session.describe());
        System.out.println("A bare `new Session()` from a static context does not compile at all.");
    }

    // ---------------------------------------------------------------- 2

    private static void hiddenOuterReference() throws Exception {
        System.out.println("Declared fields of the STATIC nested class Config:");
        printDeclaredFields(Config.class);

        System.out.println("Declared fields of the INNER class Session:");
        printDeclaredFields(Session.class);

        NestedClassesDemo outer = new NestedClassesDemo("bob");
        Session session = outer.new Session();

        Field synthetic = null;
        for (Field f : Session.class.getDeclaredFields()) {
            if (f.isSynthetic()) {
                synthetic = f;
            }
        }
        if (synthetic == null) {
            System.out.println("No synthetic field found (unexpected on this JDK).");
            return;
        }
        synthetic.setAccessible(true);
        Object referenced = synthetic.get(session);
        System.out.println("Synthetic field name: " + synthetic.getName());
        System.out.println("Does it point at the exact outer instance? " + (referenced == outer));
        System.out.println("This reference is why an inner class keeps its whole outer object reachable.");
    }

    private static void printDeclaredFields(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        if (fields.length == 0) {
            System.out.println("  (none)");
            return;
        }
        for (Field f : fields) {
            System.out.println("  " + f.getType().getSimpleName() + " " + f.getName()
                    + "  synthetic=" + f.isSynthetic());
        }
    }

    // ---------------------------------------------------------------- 3

    private final String label = "OUTER-label";

    class Shadowing {
        private final String label = "INNER-label";

        String unqualified() {
            return label;
        }

        String qualified() {
            return NestedClassesDemo.this.label;
        }
    }

    private static void shadowedField() {
        NestedClassesDemo outer = new NestedClassesDemo();
        Shadowing s = outer.new Shadowing();
        System.out.println("Unqualified `label` inside the inner class:        " + s.unqualified());
        System.out.println("Qualified `NestedClassesDemo.this.label`:          " + s.qualified());
    }

    // ---------------------------------------------------------------- 4

    /** Serializable STATIC nested class: self-contained, nothing else gets pulled in. */
    static class SafeRecordHolder implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String value;

        SafeRecordHolder(String value) {
            this.value = value;
        }
    }

    /** Serializable INNER class: the synthetic outer reference is part of its state. */
    class UnsafeRecordHolder implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String value;

        UnsafeRecordHolder(String value) {
            this.value = value;
        }
    }

    private static void serializationHazard() {
        System.out.println("Serializing the static nested SafeRecordHolder:");
        System.out.println("  " + trySerialize(new SafeRecordHolder("ok")));

        NestedClassesDemo outer = new NestedClassesDemo("carol");
        System.out.println("Serializing the inner UnsafeRecordHolder (outer is NOT Serializable):");
        System.out.println("  " + trySerialize(outer.new UnsafeRecordHolder("boom")));
    }

    private static String trySerialize(Object o) {
        try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
            out.writeObject(o);
            return "wrote " + o.getClass().getSimpleName() + " successfully";
        } catch (NotSerializableException e) {
            return "NotSerializableException: " + e.getMessage();
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    // ---------------------------------------------------------------- 5

    private static void localAndAnonymous() throws Exception {
        int multiplier = 7; // effectively final -- never reassigned

        // Local class: named, declared inside a method body.
        class Multiplier implements Callable<Integer> {
            private final int base;

            Multiplier(int base) {
                this.base = base;
            }

            @Override
            public Integer call() {
                return base * multiplier;
            }
        }

        // Anonymous class: unnamed, declared and instantiated in one expression.
        Callable<Integer> anonymous = new Callable<>() {
            @Override
            public Integer call() {
                return 100 * multiplier;
            }
        };

        System.out.println("Local class result:     " + new Multiplier(6).call());
        System.out.println("Anonymous class result: " + anonymous.call());
        System.out.println("Local class runtime name:     " + new Multiplier(1).getClass().getName());
        System.out.println("Anonymous class runtime name: " + anonymous.getClass().getName());
        System.out.println("Is the anonymous class synthetic? " + anonymous.getClass().isSynthetic()
                + "  (anonymous != synthetic -- it is a real emitted class)");
    }

    // ---------------------------------------------------------------- 6

    private static void emittedClassNames() {
        List<String> names = new ArrayList<>();
        for (Class<?> c : NestedClassesDemo.class.getDeclaredClasses()) {
            names.add(c.getName() + (java.lang.reflect.Modifier.isStatic(c.getModifiers())
                    ? "  [static nested]" : "  [inner]"));
        }
        names.sort(String::compareTo);
        names.forEach(n -> System.out.println("  " + n));
        System.out.println("  (local and anonymous classes are NOT reported by getDeclaredClasses())");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
