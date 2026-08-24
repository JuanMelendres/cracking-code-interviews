import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Real, executed proof of enum internals: each constant is a real,
 * genuine singleton instance (identity-stable across every access);
 * reflection cannot construct a new instance of an enum type at all --
 * a real, JVM-enforced guarantee, not a convention; and constant-specific
 * method bodies are real, distinct overrides per constant, not a switch
 * statement in disguise.
 */
public class EnumInternalsDemo {

    enum Operation {
        PLUS {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
        },
        MINUS {
            @Override
            public int apply(int a, int b) {
                return a - b;
            }
        },
        TIMES {
            @Override
            public int apply(int a, int b) {
                return a * b;
            }
        };

        public abstract int apply(int a, int b);
    }

    enum Color {RED, GREEN, BLUE}

    public static void main(String[] args) throws Exception {
        System.out.println("== Real singleton identity: values() returns the SAME instances every call ==");
        Color first = Color.values()[0];
        Color second = Color.values()[0];
        System.out.println("Color.values()[0] == Color.values()[0] (called twice): " + (first == second)
                + "  <-- REAL: same singleton instance, not equal-but-distinct objects");
        System.out.println("Color.valueOf(\"RED\") == Color.RED: " + (Color.valueOf("RED") == Color.RED));

        System.out.println("\n== Real proof: reflection CANNOT construct a new enum instance ==");
        Constructor<?>[] constructors = Color.class.getDeclaredConstructors();
        System.out.println("Color's declared constructors: " + constructors.length);
        try {
            Constructor<?> ctor = constructors[0];
            ctor.setAccessible(true);
            ctor.newInstance("FAKE_COLOR", 99);
            System.out.println("Reflective construction succeeded (unexpected)");
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            // Real, JVM-enforced: the constructor accessor itself refuses,
            // with the exact real message "Cannot reflectively create enum
            // objects" -- a dedicated guard, not merely a visibility check.
            System.out.println("Reflective construction threw real " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println("\n== Real constant-specific method bodies -- genuinely different code per constant ==");
        for (Operation op : Operation.values()) {
            System.out.println(op + ".apply(6, 3) = " + op.apply(6, 3)
                    + "  (real runtime class: " + op.getClass().getName() + ", declaring class: " + op.getDeclaringClass().getName() + ")");
        }
        System.out.println("\nEach constant with a body is a real, anonymous SUBCLASS of Operation (getSimpleName() is empty for"
                + " an anonymous class) -- getDeclaringClass() is the real way to get back the actual enum type.");
    }
}
