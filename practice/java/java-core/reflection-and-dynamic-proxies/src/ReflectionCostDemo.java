import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Real, measured comparison of three ways to call the same method:
 * a direct call, classic reflection (Method.invoke), and a MethodHandle
 * (the modern, JVM-optimizable alternative introduced for invokedynamic).
 * All three are verified to produce the identical result before any timing
 * claim is made.
 */
public class ReflectionCostDemo {

    static final int ITERATIONS = 200_000_000;

    public static void main(String[] args) throws Throwable {
        Calculator calc = new Calculator();

        // Correctness check first.
        int direct = calc.square(7);
        Method method = Calculator.class.getDeclaredMethod("square", int.class);
        int viaReflection = (int) method.invoke(calc, 7);
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(Calculator.class, "square", MethodType.methodType(int.class, int.class));
        int viaHandle = (int) handle.invoke(calc, 7);
        System.out.println("Correctness: direct=" + direct + " reflection=" + viaReflection + " methodHandle=" + viaHandle
                + " (all match: " + (direct == viaReflection && direct == viaHandle) + ")");

        long directElapsed = measureDirect(calc);
        long reflectionElapsed = measureReflection(calc, method);
        long handleElapsed = measureMethodHandle(calc, handle);

        System.out.println("\n== Real measured wall-clock time, " + ITERATIONS + " calls ==");
        System.out.println("Direct call:        " + directElapsed + "ms");
        System.out.println("Method.invoke():     " + reflectionElapsed + "ms ("
                + String.format("%.1fx", (double) reflectionElapsed / directElapsed) + " slower than direct)");
        System.out.println("MethodHandle.invoke(): " + handleElapsed + "ms ("
                + String.format("%.1fx", (double) handleElapsed / directElapsed) + " slower than direct)");
    }

    static long measureDirect(Calculator calc) {
        long start = System.currentTimeMillis();
        long sink = 0;
        for (int i = 0; i < ITERATIONS; i++) sink += calc.square(i);
        System.out.println("(direct sink=" + sink + ")");
        return System.currentTimeMillis() - start;
    }

    static long measureReflection(Calculator calc, Method method) throws Exception {
        long start = System.currentTimeMillis();
        long sink = 0;
        for (int i = 0; i < ITERATIONS; i++) sink += (int) method.invoke(calc, i);
        System.out.println("(reflection sink=" + sink + ")");
        return System.currentTimeMillis() - start;
    }

    static long measureMethodHandle(Calculator calc, MethodHandle handle) throws Throwable {
        long start = System.currentTimeMillis();
        long sink = 0;
        for (int i = 0; i < ITERATIONS; i++) sink += (int) handle.invoke(calc, i);
        System.out.println("(methodHandle sink=" + sink + ")");
        return System.currentTimeMillis() - start;
    }

    static class Calculator {
        int square(int x) {
            return x * x;
        }
    }
}
