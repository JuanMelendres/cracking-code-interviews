/**
 * Does NOT compile, on purpose. A static method cannot be annotated
 * @Override, because hiding is not overriding.
 *
 * Real compiler output captured in compile-errors-transcript.txt.
 */
public class BrokenStaticOverride {

    static class Base {
        static String greet() {
            return "base";
        }
    }

    static class Derived extends Base {
        @Override
        static String greet() {
            return "derived";
        }
    }
}
