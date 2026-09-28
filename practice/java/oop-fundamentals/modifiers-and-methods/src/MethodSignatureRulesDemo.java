import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Real, executed demonstration of Java's method-signature rules that
 * interviews probe: varargs, overload resolution order, generic varargs heap
 * pollution, covariant return types, and static method hiding.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/MethodSignatureRulesDemo.java
 *   java -cp out MethodSignatureRulesDemo
 */
public class MethodSignatureRulesDemo {

    public static void main(String[] args) {
        section("1. A varargs parameter is an array at runtime");
        varargsIsAnArray();

        section("2. Overload resolution runs in three phases, and varargs loses all of them");
        overloadResolutionPhases();

        section("3. An array can be passed straight to a varargs parameter -- sometimes surprisingly");
        arrayVersusVarargs();

        section("4. Generic varargs and heap pollution");
        heapPollution();

        section("5. Covariant return types");
        covariantReturns();

        section("6. Static methods are hidden, not overridden");
        staticHidingVersusOverriding();
    }

    // ---------------------------------------------------------------- 1

    static String joinAll(String separator, String... parts) {
        return parts.getClass().getSimpleName() + " of length " + parts.length
                + " -> \"" + String.join(separator, parts) + "\"";
    }

    private static void varargsIsAnArray() {
        System.out.println("joinAll(\"-\", \"a\", \"b\", \"c\") = " + joinAll("-", "a", "b", "c"));
        System.out.println("joinAll(\"-\")                   = " + joinAll("-"));
        System.out.println("A varargs call with zero arguments gets an empty array, never null.");
    }

    // ---------------------------------------------------------------- 2

    static String pick(long value) {
        return "pick(long)      [phase 1: widening primitive conversion]";
    }

    static String pick(Integer value) {
        return "pick(Integer)   [phase 2: boxing]";
    }

    static String pick(int... values) {
        return "pick(int...)    [phase 3: varargs]";
    }

    static String only(Integer value) {
        return "only(Integer)";
    }

    static String only(int... values) {
        return "only(int...)";
    }

    private static void overloadResolutionPhases() {
        int primitive = 42;
        System.out.println("pick(42) with all three overloads present:  " + pick(primitive));
        System.out.println("Phase 1 (no boxing, no varargs) already found a match, so it wins outright.");
        System.out.println();
        System.out.println("only(42) with just boxing and varargs:      " + only(primitive));
        System.out.println("Boxing (phase 2) is still preferred over varargs (phase 3).");
        System.out.println();
        System.out.println("only(1, 2) -- two arguments, only varargs applies: " + only(1, 2));
    }

    // ---------------------------------------------------------------- 3

    static String describe(Object... items) {
        return "describe() received " + items.length + " element(s)";
    }

    private static void arrayVersusVarargs() {
        String[] names = {"alice", "bob", "carol"};
        System.out.println("describe(names) where names is String[3]:      " + describe((Object[]) names));
        System.out.println("describe((Object) names) -- explicit cast:     " + describe((Object) names));
        System.out.println("A String[] IS an Object[], so it is spread into three arguments,");
        System.out.println("not wrapped as one. Casting to Object forces the single-element reading.");
        System.out.println();

        int[] numbers = {1, 2, 3};
        System.out.println("describe(numbers) where numbers is int[3]:     " + describe(numbers));
        System.out.println("int[] is NOT an Object[] (primitives do not covary), so the whole array");
        System.out.println("arrives as one element -- the same source shape, the opposite result.");
        System.out.println();
        System.out.println("Arrays.asList(numbers).size() = " + Arrays.asList(numbers).size()
                + "  <- the classic bug this causes");
        System.out.println("Arrays.asList(names).size()   = " + Arrays.asList(names).size());
    }

    // ---------------------------------------------------------------- 4

    @SafeVarargs
    static <T> List<T> listOf(T... items) {
        // Safe: the array is only read, never stored or exposed.
        return new ArrayList<>(Arrays.asList(items));
    }

    static <T> T[] unsafeToArray(T... items) {
        // Unsafe: hands the caller a reference to the generic array itself.
        return items;
    }

    private static void heapPollution() {
        System.out.println("listOf(\"x\", \"y\") = " + listOf("x", "y") + "   (@SafeVarargs, read-only use)");

        List<String>[] polluted = unsafeToArray(List.of("a"), List.of("b"));
        System.out.println("unsafeToArray(...) runtime array type: " + polluted.getClass().getSimpleName());
        System.out.println("Declared as List<String>[], but the JVM created a plain List[] --");
        System.out.println("the element type was erased, which is exactly what heap pollution means.");

        Object[] asObjects = polluted;
        asObjects[0] = List.of(1, 2, 3); // Integers into a "List<String>[]" -- no ArrayStoreException.
        try {
            String first = polluted[0].get(0);
            System.out.println("Unreachable: " + first);
        } catch (ClassCastException e) {
            System.out.println("Reading polluted[0].get(0) as String threw: "
                    + e.getClass().getSimpleName());
            System.out.println("  message: " + e.getMessage());
            System.out.println("The cast the compiler inserted failed at a line that contains no cast.");
        }
    }

    // ---------------------------------------------------------------- 5

    static class Animal {
        Animal reproduce() {
            return new Animal();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    static class Dog extends Animal {
        // Covariant return: narrower return type, still a legal override.
        @Override
        Dog reproduce() {
            return new Dog();
        }
    }

    private static void covariantReturns() {
        Animal asAnimal = new Dog();
        System.out.println("((Animal) new Dog()).reproduce() returns: " + asAnimal.reproduce());
        System.out.println("Dog.reproduce() declares Dog, not Animal -- legal since Java 5.");
        System.out.println("Callers holding a Dog reference need no cast: "
                + new Dog().reproduce().getClass().getSimpleName());
    }

    // ---------------------------------------------------------------- 6

    static class Base {
        static String staticGreet() {
            return "Base.staticGreet()";
        }

        String instanceGreet() {
            return "Base.instanceGreet()";
        }
    }

    static class Derived extends Base {
        // NOT an override -- this hides Base.staticGreet().
        static String staticGreet() {
            return "Derived.staticGreet()";
        }

        @Override
        String instanceGreet() {
            return "Derived.instanceGreet()";
        }
    }

    private static void staticHidingVersusOverriding() {
        Base viewedAsBase = new Derived();
        System.out.println("Instance method through a Base reference: " + viewedAsBase.instanceGreet()
                + "   <- dynamic dispatch, runtime type wins");
        System.out.println("Static method resolved through Base:      " + Base.staticGreet()
                + "        <- static binding, compile-time type wins");
        System.out.println("Static method resolved through Derived:   " + Derived.staticGreet());
        System.out.println("@Override on a static method is a compile error -- there is nothing to override.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
