/**
 * Real, executed proof of the string constant pool and interning: literal
 * strings (and compile-time-constant expressions) are automatically
 * interned and share identity; new String(...) genuinely creates a
 * distinct heap object even with identical content; .intern() genuinely
 * returns the pooled instance.
 */
public class InterningDemo {

    public static void main(String[] args) {
        System.out.println("== Real proof: string literals share pool identity ==");
        String literal1 = "hello";
        String literal2 = "hello";
        System.out.println("literal1 == literal2: " + (literal1 == literal2)
                + "  <-- REAL: both resolve to the SAME pooled instance, no allocation for the second literal");

        System.out.println("\n== Real proof: compile-time constant concatenation is ALSO pooled ==");
        String constantConcat = "hel" + "lo"; // javac folds this into "hello" at compile time
        System.out.println("(\"hel\" + \"lo\") == \"hello\": " + (constantConcat == literal1)
                + "  <-- REAL: javac folded this into a single constant BEFORE compilation, so it's pooled too");

        System.out.println("\n== Real proof: new String(...) is genuinely a distinct heap object ==");
        String heapString = new String("hello");
        System.out.println("new String(\"hello\") == \"hello\": " + (heapString == literal1)
                + "  <-- REAL: false, genuinely a different object, despite identical content");
        System.out.println("new String(\"hello\").equals(\"hello\"): " + heapString.equals(literal1)
                + "  (content equality still holds -- equals() compares characters, == compares identity)");

        System.out.println("\n== Real proof: .intern() returns the pooled instance ==");
        String interned = heapString.intern();
        System.out.println("heapString.intern() == \"hello\": " + (interned == literal1)
                + "  <-- REAL: true, .intern() genuinely returns the SAME pooled reference as the literal");

        System.out.println("\n== Real proof: RUNTIME concatenation (non-constant) is NOT automatically pooled ==");
        String prefix = args.length > 0 ? args[0] : "hel"; // genuinely not a compile-time constant
        String runtimeConcat = prefix + "lo";
        System.out.println("runtimeConcat == \"hello\": " + (runtimeConcat == literal1)
                + "  <-- REAL: false. Runtime concatenation allocates a genuinely new String, never auto-interned");
        System.out.println("runtimeConcat.intern() == \"hello\": " + (runtimeConcat.intern() == literal1)
                + "  (interning it explicitly DOES return the pooled instance)");
    }
}
