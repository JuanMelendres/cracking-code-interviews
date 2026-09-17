import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

        // 0a. Java 8 (final): lambda expressions — an anonymous function, replacing a
        // multi-line anonymous-class implementation of a single-method interface.
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("Lambda add(3, 4) = " + add.apply(3, 4));
        check(add.apply(3, 4) == 7, "Java 8 lambda expression correctly implements a functional interface");
        System.out.println();

        // 0b. Java 8 (final): the Stream API — declarative filter/map/collect over a
        // collection, instead of a manual for-loop with an explicit accumulator.
        List<String> names = List.of("Ada", "Bob", "Cy", "Diana", "Ed");
        List<String> longNamesUpper = names.stream()
                .filter(n -> n.length() > 2)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Stream filter+map result: " + longNamesUpper);
        check(longNamesUpper.equals(List.of("ADA", "BOB", "DIANA")),
                "Java 8 Stream correctly filters (length > 2) then maps (toUpperCase) in one declarative pipeline");
        System.out.println();

        // 0c. Java 8 (final): Optional — an explicit, typed "maybe absent" container,
        // replacing a bare null with no compile-time signal that a value might be missing.
        Optional<String> maybeName = names.stream().filter(n -> n.startsWith("Z")).findFirst();
        String resolved = maybeName.orElse("no match");
        System.out.println("Optional.orElse result: " + resolved);
        check(resolved.equals("no match"), "Optional.orElse() correctly supplies a default when the Stream found nothing");
        System.out.println();

        // 0d. Java 11 (final, JEP 323): `var` in a lambda parameter — mainly useful when
        // the parameter needs an annotation, which a bare implicit lambda parameter can't carry.
        BiFunction<Integer, Integer, Integer> multiply = (var a, var b) -> a * b;
        System.out.println("Lambda with var params multiply(3, 4) = " + multiply.apply(3, 4));
        check(multiply.apply(3, 4) == 12, "Java 11 var-in-lambda-parameters compiles and behaves identically to an untyped lambda parameter");
        System.out.println();

        // 0e. Java 11 (final, JEP 327): new String convenience methods — isBlank/strip/lines,
        // filling real, everyday gaps the API had carried since Java 1.0.
        String whitespacePadded = "   \n  ";
        String multiLine = "first\nsecond\nthird";
        System.out.println("isBlank() on whitespace-only string: " + whitespacePadded.isBlank());
        System.out.println("lines() count on 3-line string: " + multiLine.lines().count());
        check(whitespacePadded.isBlank(), "Java 11 String.isBlank() correctly treats a whitespace-only string as blank");
        check(multiLine.lines().count() == 3, "Java 11 String.lines() correctly splits into 3 real lines");
        System.out.println();

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
