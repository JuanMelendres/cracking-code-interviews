import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;

public class BasicsDemo {
    public static int square(int x) { return x * x; }

    public static class Counter {
        private int count;
        public Counter(int start) { this.count = start; }
        public int increment(int by) { count += by; return count; }
        public int get() { return count; }
    }

    public static void main(String[] args) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // findStatic
        MethodHandle squareMH = lookup.findStatic(BasicsDemo.class, "square",
                MethodType.methodType(int.class, int.class));
        int r1 = (int) squareMH.invokeExact(7);
        System.out.println("findStatic + invokeExact: square(7) = " + r1);

        // invoke() does asType coercion automatically; invokeExact() does not
        try {
            Object boxed = squareMH.invokeExact(7); // wrong return type (Object vs int)
            System.out.println("UNEXPECTED: invokeExact tolerated a type mismatch: " + boxed);
        } catch (WrongMethodTypeException e) {
            System.out.println("invokeExact with mismatched type threw WrongMethodTypeException: " + e.getMessage());
        }
        Object boxedViaInvoke = squareMH.invoke(7); // invoke() adapts automatically
        System.out.println("invoke() with mismatched-but-compatible type adapted successfully: " + boxedViaInvoke);

        // findConstructor
        MethodHandle ctorMH = lookup.findConstructor(Counter.class,
                MethodType.methodType(void.class, int.class));
        Counter c = (Counter) ctorMH.invoke(10);
        System.out.println("findConstructor: new Counter(10) -> get() = " + c.get());

        // findVirtual + bindTo
        MethodHandle incrementMH = lookup.findVirtual(Counter.class, "increment",
                MethodType.methodType(int.class, int.class));
        int afterIncrement = (int) incrementMH.invoke(c, 5);
        System.out.println("findVirtual invoke(receiver, arg): increment(5) -> " + afterIncrement);

        MethodHandle boundIncrement = incrementMH.bindTo(c);
        int afterBound = (int) boundIncrement.invoke(3);
        System.out.println("bindTo(c) then invoke(3), receiver pre-bound -> " + afterBound);
        System.out.println("bound handle type (receiver removed): " + boundIncrement.type());
        System.out.println("unbound handle type (receiver still a parameter): " + incrementMH.type());
    }
}
