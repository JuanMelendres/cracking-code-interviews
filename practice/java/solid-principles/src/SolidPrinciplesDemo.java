import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Real, compiling demonstrations of all five SOLID principles: a violation and
// a fix for each, with real, observable evidence of the difference -- not just
// assertions about "better design." Where the evidence is structural rather
// than behavioral (SRP, OCP), the demo uses reflection or literal absence of
// code to make the structural claim verifiable rather than asserted.
public class SolidPrinciplesDemo {

    // ================= SRP: Single Responsibility Principle =================

    // VIOLATION: one class, three unrelated reasons to change (validation
    // rules, persistence technology, notification channel).
    static class UserManagerViolation {
        void validate(String email) { if (!email.contains("@")) throw new IllegalArgumentException("bad email"); }
        void saveToDatabase(String email) { System.out.println("  [DB] INSERT INTO users (email) VALUES ('" + email + "')"); }
        void sendWelcomeEmail(String email) { System.out.println("  [SMTP] Sending welcome email to " + email); }
    }

    // FIXED: three classes, one reason to change each.
    static class UserValidator { void validate(String email) { if (!email.contains("@")) throw new IllegalArgumentException("bad email"); } }
    static class UserRepository { void save(String email) { System.out.println("  [DB] INSERT INTO users (email) VALUES ('" + email + "')"); } }
    static class WelcomeNotifier { void notifyUser(String email) { System.out.println("  [SMTP] Sending welcome email to " + email); } }

    static List<String> methodConcerns(Class<?> c) {
        return java.util.Arrays.stream(c.getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toList());
    }

    static void demoSrp() {
        System.out.println("--- VIOLATION: UserManagerViolation's own declared methods (one class) ---");
        System.out.println("  " + methodConcerns(UserManagerViolation.class)
                + "  <-- validation + persistence + notification, ALL in one class");
        System.out.println("--- FIXED: three separate classes, each with exactly one declared method ---");
        System.out.println("  UserValidator:    " + methodConcerns(UserValidator.class));
        System.out.println("  UserRepository:   " + methodConcerns(UserRepository.class));
        System.out.println("  WelcomeNotifier:  " + methodConcerns(WelcomeNotifier.class));
        System.out.println("A change to HOW emails are validated only touches UserValidator now --");
        System.out.println("it cannot accidentally break persistence or notification code in the same class.");
    }

    // ================= OCP: Open/Closed Principle =================

    // VIOLATION: adding a new shape means editing this method's instanceof chain.
    record CircleV(double radius) {}
    record SquareV(double side) {}
    static class AreaCalculatorViolation {
        double area(Object shape) {
            if (shape instanceof CircleV c) return Math.PI * c.radius() * c.radius();
            if (shape instanceof SquareV s) return s.side() * s.side();
            throw new IllegalArgumentException("unknown shape -- AreaCalculatorViolation must be EDITED to support it");
        }
    }

    // FIXED: new shapes implement Shape; AreaCalculatorFixed never changes.
    interface Shape { double area(); }
    record Circle(double radius) implements Shape { public double area() { return Math.PI * radius * radius; } }
    record Square(double side) implements Shape { public double area() { return side * side; } }
    static class AreaCalculatorFixed {
        double totalArea(List<Shape> shapes) { return shapes.stream().mapToDouble(Shape::area).sum(); }
    }
    // A brand-new shape, added AFTER AreaCalculatorFixed above was already
    // written and compiled -- zero lines in AreaCalculatorFixed reference it.
    record Triangle(double base, double height) implements Shape { public double area() { return 0.5 * base * height; } }

    static void demoOcp() {
        AreaCalculatorViolation v = new AreaCalculatorViolation();
        System.out.println("--- VIOLATION: works for existing shapes ---");
        System.out.printf("  circle area=%.2f, square area=%.2f%n", v.area(new CircleV(2)), v.area(new SquareV(3)));
        System.out.println("  (adding Triangle support here requires EDITING AreaCalculatorViolation.area())");

        List<Shape> shapes = new ArrayList<>(List.of(new Circle(2), new Square(3)));
        AreaCalculatorFixed fixed = new AreaCalculatorFixed();
        System.out.println("--- FIXED: same shapes, via AreaCalculatorFixed (never touched since first written) ---");
        System.out.printf("  total area (circle+square) = %.2f%n", fixed.totalArea(shapes));

        shapes.add(new Triangle(4, 5));
        System.out.println("--- FIXED: added Triangle -- a NEW class -- zero changes to AreaCalculatorFixed's source ---");
        System.out.printf("  total area (circle+square+triangle) = %.2f%n", fixed.totalArea(shapes));
    }

    // ================= LSP: Liskov Substitution Principle =================

    // VIOLATION: Square-extends-Rectangle breaks the base type's own invariant.
    static class Rectangle {
        protected int width, height;
        void setWidth(int w) { this.width = w; }
        void setHeight(int h) { this.height = h; }
        int area() { return width * height; }
    }
    static class SquareLsp extends Rectangle {
        @Override void setWidth(int w) { this.width = w; this.height = w; }
        @Override void setHeight(int h) { this.width = h; this.height = h; }
    }

    static void assertRectangleInvariant(Rectangle r, String label) {
        r.setWidth(5);
        r.setHeight(4);
        int expected = 20;
        int actual = r.area();
        System.out.println("  " + label + ": setWidth(5); setHeight(4); expected area=" + expected
                + ", actual area=" + actual + (actual == expected ? "  OK" : "  VIOLATION -- setHeight silently changed width too"));
    }

    static void demoLsp() {
        System.out.println("--- Any code written against Rectangle should hold for ANY Rectangle, per LSP ---");
        assertRectangleInvariant(new Rectangle(), "Rectangle ");
        assertRectangleInvariant(new SquareLsp(), "SquareLsp ");
        System.out.println("SquareLsp IS-A Rectangle per the type system, but breaks code written against Rectangle's own contract.");
        System.out.println("--- FIXED: no inheritance relationship at all -- Square and Rectangle both just implement Shape ---");
        Shape fixedSquare = new Square(5);
        System.out.println("  Square(5).area() = " + fixedSquare.area() + "  (no shared mutable base type to violate)");
    }

    // ================= ISP: Interface Segregation Principle =================

    // VIOLATION: a fat interface forces an implementer to support methods it can't.
    interface WorkerViolation { void work(); void eat(); }
    static class RobotWorkerViolation implements WorkerViolation {
        public void work() { System.out.println("  Robot working."); }
        public void eat() { throw new UnsupportedOperationException("Robots don't eat"); }
    }

    // FIXED: split into role-specific interfaces; a robot only implements what applies.
    interface Workable { void work(); }
    interface Eatable { void eat(); }
    static class RobotWorkerFixed implements Workable {
        public void work() { System.out.println("  Robot working."); }
        // no eat() method exists here at all -- not even a stub
    }

    static void demoIsp() {
        RobotWorkerViolation rv = new RobotWorkerViolation();
        rv.work();
        System.out.println("--- VIOLATION: RobotWorkerViolation.eat() must exist to satisfy WorkerViolation, and throws ---");
        try {
            rv.eat();
        } catch (UnsupportedOperationException e) {
            System.out.println("  Caught: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("--- FIXED: RobotWorkerFixed implements only Workable -- no eat() method exists to call at all ---");
        System.out.println("  RobotWorkerFixed's declared methods: " + methodConcerns(RobotWorkerFixed.class));
        new RobotWorkerFixed().work();
    }

    // ================= DIP: Dependency Inversion Principle =================

    // VIOLATION: high-level policy directly constructs a concrete low-level detail.
    static class MySqlOrderRepositoryViolation {
        void save(String order) { System.out.println("  [MySQL] INSERT order: " + order); }
    }
    static class OrderServiceViolation {
        private final MySqlOrderRepositoryViolation repo = new MySqlOrderRepositoryViolation(); // hardcoded
        void placeOrder(String order) { repo.save(order); }
    }

    // FIXED: high-level policy depends on an abstraction, injected.
    interface OrderRepository { void save(String order); }
    static class MySqlOrderRepository implements OrderRepository {
        public void save(String order) { System.out.println("  [MySQL] INSERT order: " + order); }
    }
    static class InMemoryOrderRepository implements OrderRepository {
        List<String> saved = new ArrayList<>();
        public void save(String order) { saved.add(order); System.out.println("  [InMemory] stored order: " + order + " (list size=" + saved.size() + ")"); }
    }
    static class OrderServiceFixed {
        private final OrderRepository repo;
        OrderServiceFixed(OrderRepository repo) { this.repo = repo; } // constructor-injected
        void placeOrder(String order) { repo.save(order); }
    }

    static void demoDip() {
        System.out.println("--- VIOLATION: OrderServiceViolation can ONLY ever use MySQL -- it constructs it itself ---");
        new OrderServiceViolation().placeOrder("order-1");

        System.out.println("--- FIXED: identical OrderServiceFixed class, swapped repository via constructor injection ---");
        OrderServiceFixed prod = new OrderServiceFixed(new MySqlOrderRepository());
        prod.placeOrder("order-2");
        OrderServiceFixed test = new OrderServiceFixed(new InMemoryOrderRepository());
        test.placeOrder("order-3");
        System.out.println("  Same OrderServiceFixed.java, zero changes -- only the injected implementation differs.");
    }

    public static void main(String[] args) {
        System.out.println("############ S -- Single Responsibility Principle ############");
        demoSrp();
        System.out.println();
        System.out.println("############ O -- Open/Closed Principle ############");
        demoOcp();
        System.out.println();
        System.out.println("############ L -- Liskov Substitution Principle ############");
        demoLsp();
        System.out.println();
        System.out.println("############ I -- Interface Segregation Principle ############");
        demoIsp();
        System.out.println();
        System.out.println("############ D -- Dependency Inversion Principle ############");
        demoDip();
    }
}
