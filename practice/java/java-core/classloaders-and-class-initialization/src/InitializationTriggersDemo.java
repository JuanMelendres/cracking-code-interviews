/**
 * Real, executed proof of exactly which operations trigger class
 * initialization (running static initializer blocks/static field
 * initializers) and which don't -- the JLS's "active use" rules, verified
 * directly rather than assumed.
 */
public class InitializationTriggersDemo {

    static class HasNonConstantStatic {
        static {
            System.out.println("  [HasNonConstantStatic] static initializer RAN");
        }
        static int counter = computeInitialValue(); // NOT a compile-time constant
        static final String NAME = "widget"; // also not compile-time-foldable (calls a method... actually literal is constant)

        static int computeInitialValue() {
            return 42;
        }
    }

    static class HasCompileTimeConstant {
        static {
            System.out.println("  [HasCompileTimeConstant] static initializer RAN");
        }
        static final int CONSTANT = 100; // a real compile-time constant -- inlined by javac at every use site
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Trigger 1: merely referencing a class in a type declaration -- does NOT initialize ==");
        Class<HasNonConstantStatic> typeRef = HasNonConstantStatic.class;
        System.out.println("  Referenced HasNonConstantStatic.class as a type -- no \"static initializer RAN\" printed above this line? "
                + "(check: nothing should have printed yet)");

        System.out.println("\n== Trigger 2: Class.forName() with initialize=false -- does NOT initialize ==");
        Class.forName(HasNonConstantStatic.class.getName(), false, InitializationTriggersDemo.class.getClassLoader());
        System.out.println("  Class.forName(..., initialize=false) completed -- still no initializer output expected above");

        System.out.println("\n== Trigger 3: accessing a compile-time CONSTANT static final field -- does NOT initialize ==");
        int constant = HasCompileTimeConstant.CONSTANT; // javac inlines this; no real class-loading trigger
        System.out.println("  Read HasCompileTimeConstant.CONSTANT = " + constant + " -- still no initializer output expected above"
                + " (javac inlined the literal directly into THIS class's own bytecode)");

        System.out.println("\n== Trigger 4: accessing a NON-constant static field -- DOES initialize, real output below ==");
        int value = HasNonConstantStatic.counter; // a genuine "active use" -- real trigger
        System.out.println("  Read HasNonConstantStatic.counter = " + value + " (the print above this line is the real, triggered initializer)");

        System.out.println("\n== Trigger 5: constructing an instance -- DOES initialize (if not already) ==");
        new HasCompileTimeConstant(); // first REAL active use of this class -- triggers it now
        System.out.println("  Constructed a HasCompileTimeConstant instance (the print above this line is the real, triggered initializer,"
                + " which did NOT run back at Trigger 3 despite referencing the same class)");
    }
}
