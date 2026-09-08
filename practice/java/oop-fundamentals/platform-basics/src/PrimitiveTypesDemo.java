import java.util.ArrayList;
import java.util.List;

public class PrimitiveTypesDemo {

    static int assertionCount = 0;
    static int passCount = 0;

    static void check(boolean condition, String label) {
        assertionCount++;
        if (condition) {
            passCount++;
            System.out.println("PASS: " + label);
        } else {
            System.out.println("FAIL: " + label);
        }
    }

    public static void main(String[] args) {

        // 1. All eight primitive types, with real Java-guaranteed ranges.
        byte b = 127;
        short s = 32000;
        int i = 2_000_000_000;
        long l = 9_000_000_000_000_000_000L;
        float f = 3.14f;
        double d = 3.14159265358979;
        char c = 'A';
        boolean flag = true;

        System.out.println("byte:    " + b + "  (range " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE + ", 1 byte)");
        System.out.println("short:   " + s + "  (range " + Short.MIN_VALUE + " to " + Short.MAX_VALUE + ", 2 bytes)");
        System.out.println("int:     " + i + "  (range " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + ", 4 bytes)");
        System.out.println("long:    " + l + "  (range " + Long.MIN_VALUE + " to " + Long.MAX_VALUE + ", 8 bytes)");
        System.out.println("float:   " + f + "  (~7 decimal digits precision, 4 bytes)");
        System.out.println("double:  " + d + "  (~15 decimal digits precision, 8 bytes)");
        System.out.println("char:    " + c + "  (code point " + (int) c + ", range 0 to 65535, 2 bytes, UTF-16 code unit)");
        System.out.println("boolean: " + flag + "  (JVM-dependent size, not specified by the JLS)");
        System.out.println();

        // 2. Real, measured overflow: byte wraps around silently, no exception.
        byte maxByte = Byte.MAX_VALUE; // 127
        byte overflowed = (byte) (maxByte + 1);
        System.out.println("Byte.MAX_VALUE + 1, cast back to byte = " + overflowed);
        check(overflowed == -128, "byte overflow wraps 127 + 1 to -128, silently, no exception");
        System.out.println();

        // 3. int overflow: the classic silent-wraparound bug.
        int maxInt = Integer.MAX_VALUE; // 2147483647
        int intOverflowed = maxInt + 1;
        System.out.println("Integer.MAX_VALUE + 1 = " + intOverflowed);
        check(intOverflowed == Integer.MIN_VALUE, "int overflow wraps MAX_VALUE + 1 to MIN_VALUE, silently");
        System.out.println();

        // 4. float/double imprecision: the real 0.1 + 0.2 surprise.
        double sum = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + sum);
        check(sum != 0.3, "0.1 + 0.2 does NOT exactly equal 0.3 due to binary floating-point representation");
        check(Math.abs(sum - 0.3) < 1e-9, "but it IS extremely close to 0.3 — the error is in the last few bits");
        System.out.println();

        // 5. Autoboxing / unboxing: primitive <-> wrapper class conversion.
        int primitiveInt = 42;
        Integer boxedInt = primitiveInt; // autoboxing
        int unboxedInt = boxedInt;        // auto-unboxing
        System.out.println("primitive int " + primitiveInt + " autoboxed to Integer " + boxedInt
                + ", then unboxed back to int " + unboxedInt);
        check(unboxedInt == primitiveInt, "round-trip autoboxing/unboxing preserves the value");
        System.out.println();

        // 6. THE real, measured Integer cache gotcha: -128..127 are cached, outside that range they are not.
        Integer cachedA = 127;
        Integer cachedB = 127;
        Integer notCachedA = 128;
        Integer notCachedB = 128;
        System.out.println("Integer 127 == Integer 127 (both from cache): " + (cachedA == cachedB));
        System.out.println("Integer 128 == Integer 128 (both NEW objects): " + (notCachedA == notCachedB));
        check(cachedA == cachedB, "Integer.valueOf caches -128..127, so two 127s are the SAME object (== is true)");
        check(notCachedA != notCachedB, "128 is outside the cache range, so two 128s are DIFFERENT objects (== is false)");
        check(notCachedA.equals(notCachedB), "but .equals() correctly reports them as equal in VALUE");
        System.out.println();

        // 7. Wrapper classes carry real static utility methods primitives don't have.
        String parsed = "123";
        int fromString = Integer.parseInt(parsed);
        String backToString = Integer.toString(fromString);
        System.out.println("Integer.parseInt(\"123\") = " + fromString + ", Integer.toString(123) = \"" + backToString + "\"");
        check(fromString == 123, "Integer.parseInt correctly parses a numeric string");
        check(backToString.equals("123"), "Integer.toString correctly formats back to a string");
        System.out.println();

        // 8. A real NullPointerException from unboxing a null wrapper — a genuine production bug shape.
        List<Integer> maybeNulls = new ArrayList<>();
        maybeNulls.add(5);
        maybeNulls.add(null);
        int caughtNpe = 0;
        for (Integer value : maybeNulls) {
            try {
                int unboxed = value; // NPE on the null element during auto-unboxing
                System.out.println("Unboxed successfully: " + unboxed);
            } catch (NullPointerException e) {
                caughtNpe++;
                System.out.println("Caught real NullPointerException while auto-unboxing a null Integer");
            }
        }
        check(caughtNpe == 1, "auto-unboxing a null wrapper throws a real NullPointerException, not a silent 0");

        System.out.println();
        System.out.println(passCount + " / " + assertionCount + " assertions passed");
    }
}
