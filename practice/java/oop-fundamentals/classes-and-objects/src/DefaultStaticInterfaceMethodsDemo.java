public class DefaultStaticInterfaceMethodsDemo {

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

    // A real interface static method — belongs to the interface itself, called as
    // InterfaceName.method(), never through an instance reference.
    interface MathOps {
        static int square(int x) {
            return x * x;
        }

        // A real default method — carries an actual body, inherited as-is unless overridden.
        default int cube(int x) {
            return x * x * x;
        }
    }

    static class Calculator implements MathOps {
        // Deliberately does NOT override cube() — inherits MathOps's default body as-is.
    }

    public static void main(String[] args) {

        // 1. A real interface static method call — via the interface name, not an instance.
        int squared = MathOps.square(5);
        System.out.println("MathOps.square(5) = " + squared);
        check(squared == 25, "interface static method is called through the interface name directly");

        // 2. A real default method, inherited unmodified by a concrete implementing class.
        Calculator calc = new Calculator();
        int cubed = calc.cube(3);
        System.out.println("new Calculator().cube(3) = " + cubed + " (inherited from MathOps's default body, unmodified)");
        check(cubed == 27, "a concrete class inherits an interface's default method body without overriding it");

        System.out.println();
        System.out.println(passCount + " / " + assertionCount + " assertions passed");
    }
}
