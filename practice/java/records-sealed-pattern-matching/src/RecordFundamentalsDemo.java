import java.util.Objects;

// Real, executed demo of what a record actually generates, and what the compact
// constructor is for. No framework, no libraries -- plain javac/java, JDK 21.
public class RecordFundamentalsDemo {

    // A record's canonical constructor, equals(), hashCode(), and toString() are
    // generated from the component list -- Point(x, y). Nothing hand-written below.
    record Point(int x, int y) {}

    // A compact constructor runs BEFORE the implicit field assignment. It can
    // validate or normalize, but cannot skip assigning every component -- the
    // assignment to `this.x`/`this.y` is inserted by the compiler right after.
    record Range(int lo, int hi) {
        Range {
            if (lo > hi) {
                throw new IllegalArgumentException("lo (" + lo + ") > hi (" + hi + ")");
            }
        }
    }

    // Records can implement interfaces (they just can't extend a class -- every
    // record implicitly extends java.lang.Record, so single inheritance is spent).
    interface HasArea {
        double area();
    }

    record Rectangle(double width, double height) implements HasArea {
        @Override
        public double area() {
            return width * height;
        }
    }

    public static void main(String[] args) {
        System.out.println("== 1. Auto-generated equals()/hashCode()/toString() ==");
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(3, 5);
        System.out.println("p1 = " + p1);
        System.out.println("p1.equals(p2) [same components, different instances] = " + p1.equals(p2));
        System.out.println("p1.hashCode() == p2.hashCode() = " + (p1.hashCode() == p2.hashCode()));
        System.out.println("p1.equals(p3) [different y] = " + p1.equals(p3));
        System.out.println("p1 == p2 [reference identity] = " + (p1 == p2));
        System.out.println("Independently derived hash matches: " + (p1.hashCode() == Objects.hash(3, 4)));

        System.out.println();
        System.out.println("== 2. Accessors are x()/y(), not getX()/getY() ==");
        System.out.println("p1.x() = " + p1.x() + ", p1.y() = " + p1.y());

        System.out.println();
        System.out.println("== 3. Compact constructor: valid range ==");
        Range r = new Range(2, 9);
        System.out.println("new Range(2, 9) = " + r);

        System.out.println();
        System.out.println("== 4. Compact constructor: invalid range throws, real stack trace ==");
        try {
            new Range(9, 2);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println();
        System.out.println("== 5. Records CAN implement interfaces ==");
        Rectangle rect = new Rectangle(3.0, 4.0);
        HasArea area = rect;
        System.out.println("rect.area() via interface reference = " + area.area());

        System.out.println();
        System.out.println("== 6. Records are implicitly final and implicitly extend java.lang.Record ==");
        System.out.println("Point.class.getSuperclass() = " + Point.class.getSuperclass());
        System.out.println("Modifier.isFinal(Point.class.getModifiers()) = "
            + java.lang.reflect.Modifier.isFinal(Point.class.getModifiers()));
    }
}
