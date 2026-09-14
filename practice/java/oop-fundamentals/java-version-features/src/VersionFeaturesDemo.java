import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class VersionFeaturesDemo {

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

    // Java 16 (final, JEP 395): records — a real, compact, immutable data carrier.
    record Point(int x, int y) {
        // Records can still have real methods, including validation in a compact constructor.
        Point {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("coordinates must be non-negative");
            }
        }
    }

    // Java 17 (final, JEP 409): sealed classes — a closed, exhaustively-known set of subtypes.
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    // Java 21 (final, JEP 441): pattern matching for switch, exhaustive over a sealed type
    // with NO default branch required, because the compiler can prove Circle/Square are the only cases.
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
        };
    }

    public static void main(String[] args) throws Exception {

        // 1. Java 10 (final, JEP 286): local-variable type inference with `var`.
        var message = "var infers String here, at compile time — this is NOT dynamic typing";
        var numbers = List.of(1, 2, 3, 4, 5);
        System.out.println(message);
        check(message instanceof String, "var-declared 'message' is a real, statically-typed String");
        System.out.println();

        // 2. Java 15 (final, JEP 378): text blocks — real multi-line string literals.
        String textBlock = """
                {
                  "name": "text blocks",
                  "since": "Java 15"
                }""";
        System.out.println(textBlock);
        check(textBlock.contains("text blocks"), "text block correctly preserves real multi-line JSON-shaped content");
        System.out.println();

        // 3. Java 16 (final, JEP 395): records — real, compiler-generated equals/hashCode/toString.
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        System.out.println("p1 = " + p1 + ", p1.equals(p2) = " + p1.equals(p2));
        check(p1.equals(p2), "records get real, correct, compiler-generated equals() based on components");
        check(p1.x() == 3 && p1.y() == 4, "records get real, compiler-generated accessors (x(), y()) automatically");
        boolean validationTriggered = false;
        try {
            new Point(-1, 0);
        } catch (IllegalArgumentException e) {
            validationTriggered = true;
        }
        check(validationTriggered, "a record's compact constructor can still enforce real validation");
        System.out.println();

        // 4. Java 16 (final, JEP 394): pattern matching for instanceof — no separate cast needed.
        Object maybePoint = p1;
        if (maybePoint instanceof Point p && p.x() > 0) {
            System.out.println("Pattern-matched instanceof: p.x() = " + p.x() + ", p.y() = " + p.y());
            check(true, "pattern matching for instanceof binds 'p' directly, no separate cast statement needed");
        }
        System.out.println();

        // 5. Java 17 (final, JEP 409) + Java 21 (final, JEP 441): sealed types + exhaustive switch.
        Shape circle = new Circle(2.0);
        Shape square = new Square(3.0);
        System.out.println("Circle area: " + area(circle) + ", Square area: " + area(square));
        check(Math.abs(area(circle) - (Math.PI * 4)) < 1e-9, "exhaustive switch over a sealed type correctly computes Circle's area");
        check(area(square) == 9.0, "exhaustive switch over a sealed type correctly computes Square's area, with NO default branch");
        System.out.println();

        // 6. Java 21 (final, JEP 444): virtual threads — real, lightweight, JVM-managed threads.
        AtomicInteger completed = new AtomicInteger(0);
        int taskCount = 1000;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new java.util.ArrayList<>();
            for (int t = 0; t < taskCount; t++) {
                futures.add(executor.submit(() -> {
                    completed.incrementAndGet();
                }));
            }
            for (Future<?> f : futures) {
                f.get();
            }
        }
        System.out.println("Completed " + completed.get() + " / " + taskCount + " tasks, each on its own real virtual thread");
        check(completed.get() == taskCount, "1000 real virtual threads all completed — this would be a heavy real OS-thread cost pre-Java 21");

        System.out.println();
        System.out.println(passCount + " / " + assertionCount + " assertions passed");
    }
}
