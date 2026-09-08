public class StaticFinalDemo {

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

    // A real, measured demonstration: static state is shared across ALL instances;
    // instance state is separate per instance.
    static class Counter {
        static int totalCreated = 0; // shared across every Counter instance
        int instanceId;              // separate for each Counter instance

        Counter() {
            totalCreated++;
            instanceId = totalCreated;
        }
    }

    // A real final field, assigned once (either at declaration or in the constructor).
    static class ImmutablePoint {
        final int x;
        final int y;

        ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // A real abstract class with BOTH an abstract method (no body, subclass must implement)
    // and a concrete method (a real body, inherited as-is unless a subclass overrides it).
    abstract static class Shape {
        abstract double area(); // abstract method: signature only, no body, no braces

        // concrete method: has a real body, callable directly, can also be overridden
        String describe() {
            return "A shape with area " + area();
        }
    }

    static class Circle extends Shape {
        final double radius;

        Circle(double radius) {
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }
    }

    public static void main(String[] args) {

        // 1. Static state is shared; instance state is not.
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        System.out.println("c1.instanceId = " + c1.instanceId + ", c2.instanceId = " + c2.instanceId
                + ", c3.instanceId = " + c3.instanceId);
        System.out.println("Counter.totalCreated = " + Counter.totalCreated);
        check(c1.instanceId != c2.instanceId, "each instance gets its OWN instanceId (instance state is separate)");
        check(Counter.totalCreated == 3, "totalCreated is SHARED and incremented by every instance (static state)");
        check(c1.totalCreated == c2.totalCreated, "c1.totalCreated and c2.totalCreated are literally the same field");
        System.out.println();

        // 2. final fields hold their value for the object's whole lifetime once assigned.
        ImmutablePoint p = new ImmutablePoint(3, 4);
        System.out.println("ImmutablePoint(" + p.x + ", " + p.y + ") created — x and y can never be reassigned");
        check(p.x == 3 && p.y == 4, "final fields correctly hold their constructor-assigned values");
        System.out.println();

        // 3. Abstract vs. concrete method signatures, demonstrated on a real object.
        Circle circle = new Circle(2.0);
        System.out.println(circle.describe()); // uses Shape's concrete describe(), calling Circle's abstract area()
        check(Math.abs(circle.area() - (Math.PI * 4)) < 1e-9, "Circle correctly implements Shape's abstract area() method");
        check(circle.describe().contains("area"), "Circle inherits Shape's concrete describe() method as-is, unmodified");
        System.out.println();

        System.out.println(passCount + " / " + assertionCount + " assertions passed");
    }
}
