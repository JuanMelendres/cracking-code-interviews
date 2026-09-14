import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates inheritance and abstraction as distinct, complementary tools:
 * inheritance shares a base implementation across a family of related
 * classes ("is-a"); abstraction defines a contract without committing to an
 * implementation. An abstract class and an interface are compared directly
 * against the same problem to show why they answer different questions.
 * (Runtime method-dispatch mechanics are covered in depth in
 * polymorphism-and-dynamic-dispatch.md — this demo only uses the outcome:
 * calling shape.area() on a mixed list runs each shape's own version.)
 */
public class AbstractionAndInheritanceDemo {

    // --- Abstraction, tool 1: abstract class — shares real implementation code ---
    abstract static class Shape {
        private final String name;

        Shape(String name) {
            this.name = name; // shared constructor logic every subclass inherits
        }

        String getName() {
            return name; // a concrete method every subclass gets for free
        }

        abstract double area(); // no implementation — each subclass MUST supply one
    }

    static class Circle extends Shape {
        private final double radius;

        Circle(double radius) {
            super("Circle"); // inheritance: must run the parent's constructor first
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }
    }

    static class Rectangle extends Shape {
        private final double width;
        private final double height;

        Rectangle(double width, double height) {
            super("Rectangle");
            this.width = width;
            this.height = height;
        }

        @Override
        double area() {
            return width * height;
        }
    }

    // --- Abstraction, tool 2: interface — a pure contract, no shared state at all ---
    interface Movable {
        void move(double dx, double dy);

        default String describe() {
            // an interface CAN carry a default method (since Java 8), but it
            // still has no instance fields of its own — no shared state,
            // only shared behavior derived from the implementor's own methods
            return "A movable object";
        }
    }

    static class MovableCircle extends Circle implements Movable {
        private double x, y;

        MovableCircle(double radius, double x, double y) {
            super(radius);
            this.x = x;
            this.y = y;
        }

        @Override
        public void move(double dx, double dy) {
            x += dx;
            y += dy;
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }
    }
    // MovableCircle proves the actual difference in practice: it EXTENDS one
    // class (Circle — single inheritance, Java allows only one) and
    // IMPLEMENTS one interface here, but could implement several more
    // (Movable, Resizable, Drawable, ...) with no conflict, because an
    // interface never contributes constructor logic or instance state to
    // resolve — only a method contract.

    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(boolean condition, String description) {
        assertions++;
        if (!condition) {
            failures.add(description);
        }
    }

    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle(2.0));
        shapes.add(new Rectangle(3.0, 4.0));

        double totalArea = 0;
        for (Shape s : shapes) {
            totalArea += s.area(); // each call runs that specific shape's own area()
        }
        check(Math.abs(totalArea - (Math.PI * 4 + 12)) < 0.0001,
                "summing area() over a mixed List<Shape> correctly calls each subclass's own implementation");

        check(shapes.get(0).getName().equals("Circle"),
                "getName() is inherited unchanged from the abstract Shape base class");
        check(shapes.get(1).getName().equals("Rectangle"),
                "getName() works identically for a different subclass, same inherited method");

        MovableCircle mc = new MovableCircle(1.0, 0.0, 0.0);
        mc.move(5.0, -3.0);
        check(mc.getX() == 5.0 && mc.getY() == -3.0,
                "a class can extend a concrete class AND implement an interface at the same time");
        check(mc.describe().equals("A movable object"),
                "an interface's default method runs even though MovableCircle never overrides it");
        check(mc.area() > 0, "MovableCircle still has the area() behavior inherited through Circle -> Shape");

        // The line "abstract Shape s = new Shape(\"x\");" is not written here at
        // all because it would not compile — an abstract class can never be
        // instantiated directly, only through a concrete subclass. That is
        // the actual mechanism, not a style guideline.

        System.out.println((failures.isEmpty() ? "PASS" : "FAIL") + " " + (assertions - failures.size()) + "/" + assertions + " assertions");
        for (String f : failures) {
            System.out.println("  FAILED: " + f);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }
}
