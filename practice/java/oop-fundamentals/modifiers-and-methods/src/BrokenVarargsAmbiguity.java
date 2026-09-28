/**
 * Does NOT compile, on purpose. Two varargs overloads are equally applicable
 * to the same call, so no single most-specific method exists.
 *
 * Real compiler output captured in compile-errors-transcript.txt.
 */
public class BrokenVarargsAmbiguity {

    static void handle(String first, Object... rest) {
        System.out.println("handle(String, Object...)");
    }

    static void handle(Object first, String... rest) {
        System.out.println("handle(Object, String...)");
    }

    public static void main(String[] args) {
        handle("a", "b"); // ambiguous: neither overload is more specific
    }
}
