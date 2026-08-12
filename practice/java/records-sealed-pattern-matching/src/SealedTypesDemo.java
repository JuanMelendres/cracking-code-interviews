// Real, executed demo of sealed interfaces/classes and compiler-enforced
// exhaustiveness in switch expressions. JDK 21.
public class SealedTypesDemo {

    // `permits` is a closed, compiler-known list. Nothing outside this file can
    // add a fourth implementation -- the compiler rejects it (permits requires
    // same module/package unless subclasses are also declared `sealed` or `final`
    // and re-permit further).
    sealed interface Shape permits Circle, Rectangle, Triangle {}

    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    // Because Shape is sealed and every permitted subtype is a record (hence
    // implicitly final), the compiler can PROVE this switch is exhaustive over
    // every possible Shape value. No `default` branch, and no runtime risk of
    // silently falling through to nothing -- if a case is missing, this is a
    // COMPILE ERROR, not a runtime bug waiting to happen.
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    public static void main(String[] args) {
        System.out.println("== Exhaustive switch over a sealed hierarchy, no default branch ==");
        Shape[] shapes = { new Circle(2), new Rectangle(3, 4), new Triangle(6, 5) };
        for (Shape s : shapes) {
            System.out.printf("%-25s area = %.4f%n", s, area(s));
        }

        System.out.println();
        System.out.println("== This compiled ONLY because the compiler proved all 3 permitted");
        System.out.println("   subtypes are handled. See sealed-exhaustiveness-compile-error.txt");
        System.out.println("   in this same directory for the real javac error produced when a");
        System.out.println("   4th permitted subtype is added without updating this switch. ==");
    }
}
