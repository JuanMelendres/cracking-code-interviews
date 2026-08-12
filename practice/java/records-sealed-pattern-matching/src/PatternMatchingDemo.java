// Real, executed demo of pattern matching evolution: instanceof patterns (JDK 16),
// switch type patterns + guarded patterns + null handling (JDK 21), and record
// patterns / nested deconstruction (JDK 21). All final features, no --enable-preview.
public class PatternMatchingDemo {

    sealed interface Shape permits Circle, Rectangle {}
    record Point(int x, int y) {}
    record Circle(Point center, double radius) implements Shape {}
    record Rectangle(Point topLeft, Point bottomRight) implements Shape {}

    public static void main(String[] args) {
        System.out.println("== 1. instanceof pattern matching (JDK 16): binds AND narrows in one step ==");
        Object obj = "narrowed to String";
        if (obj instanceof String s && s.length() > 5) {
            // pre-JDK-16 this needed: String s = (String) obj; as a separate line.
            System.out.println("Matched, s.length() = " + s.length() + ", s.toUpperCase() = " + s.toUpperCase());
        }

        System.out.println();
        System.out.println("== 2. switch type patterns (JDK 21): replaces instanceof chains ==");
        for (Object o : new Object[]{42, "hi", 3.14, 100L}) {
            System.out.println(describe(o));
        }

        System.out.println();
        System.out.println("== 3. Guarded patterns with `when` (JDK 21): pattern + extra boolean condition ==");
        for (Integer score : new Integer[]{95, 72, 40}) {
            System.out.println(score + " -> " + grade(score));
        }

        System.out.println();
        System.out.println("== 4. `case null` in switch (JDK 21): explicit, compiler-checked null handling ==");
        for (String s : new String[]{"hello", null}) {
            System.out.println(describeNullable(s));
        }

        System.out.println();
        System.out.println("== 5. Record patterns: nested deconstruction (JDK 21) ==");
        Shape[] shapes = {
            new Circle(new Point(0, 0), 5),
            new Rectangle(new Point(0, 0), new Point(4, 4)),
            new Circle(new Point(3, 3), 5)
        };
        for (Shape s : shapes) {
            System.out.println(describeShape(s));
        }
    }

    static String describe(Object o) {
        return switch (o) {
            case Integer i -> "Integer: " + i + " (doubled: " + (i * 2) + ")";
            case String s -> "String: \"" + s + "\" (length " + s.length() + ")";
            case Long l -> "Long: " + l;
            default -> "Something else: " + o;
        };
    }

    static String grade(Integer score) {
        return switch (score) {
            case Integer s when s >= 90 -> "A";
            case Integer s when s >= 70 -> "B";
            default -> "C or below";
        };
    }

    static String describeNullable(String s) {
        return switch (s) {
            case null -> "was null -- handled explicitly, no NullPointerException";
            case String str when str.isBlank() -> "was blank";
            case String str -> "was: \"" + str + "\"";
        };
    }

    // Nested record pattern: deconstructs a Circle straight down to its center's
    // x/y coordinates in the case label itself -- no manual c.center().x() calls.
    static String describeShape(Shape shape) {
        return switch (shape) {
            case Circle(Point(var x, var y), var radius) when x == 0 && y == 0 ->
                "Circle centered at origin, radius " + radius;
            case Circle(Point(var x, var y), var radius) ->
                "Circle at (" + x + ", " + y + "), radius " + radius;
            case Rectangle(Point(var x1, var y1), Point(var x2, var y2)) ->
                "Rectangle from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + "), area "
                    + (Math.abs(x2 - x1) * Math.abs(y2 - y1));
        };
    }
}
