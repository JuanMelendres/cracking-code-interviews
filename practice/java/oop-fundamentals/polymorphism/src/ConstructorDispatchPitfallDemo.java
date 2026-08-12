// Java 21. Dynamic dispatch is still fully in effect DURING object
// construction -- a superclass constructor that calls an overridable method
// invokes the SUBCLASS's override, even though the subclass's own field
// initializers haven't run yet. This is the classic "constructor calling an
// overridable method" pitfall: the override sees the subclass in a partially
// -constructed state, often with fields still at their default value.

abstract class Report {
    Report() {
        System.out.println("Report() constructor running -- about to call describe()...");
        System.out.println("describe() returned: " + describe());
    }

    abstract String describe();
}

class SalesReport extends Report {
    // NOT `final` with a bare string literal on purpose: a `final` field
    // initialized directly from a compile-time constant expression (a string
    // literal, here) is itself a compile-time constant per the JLS, and javac
    // is free to inline that constant at every read site -- which would mask
    // this exact pitfall instead of demonstrating it. Building the value via
    // StringBuilder keeps it a genuine, non-constant instance initializer.
    private String title = new StringBuilder("Q3 Sales").toString();

    @Override
    String describe() {
        // At the moment Report()'s constructor calls this, title's initializer
        // has NOT run yet -- Java has only zero-initialized it (null for a
        // reference field), because field initializers run after the super()
        // call returns, not before it starts.
        return "title=" + title;
    }
}

class ConstructorDispatchPitfallDemo {
    public static void main(String[] args) {
        System.out.println("== Constructing a SalesReport ==");
        SalesReport report = new SalesReport();
        System.out.println();
        System.out.println("== After construction completes ==");
        System.out.println("report.describe() now returns: " + report.describe() + "  (title is now properly initialized)");
    }
}
