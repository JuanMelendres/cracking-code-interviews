// Java 21. Fields are NOT polymorphic -- there is no vtable lookup for field
// access. A field read is resolved entirely by the reference's declared
// (compile-time) type, baked in at compile time as a direct offset access.
// A subclass that declares a field with the same name doesn't override it,
// it HIDES it -- both fields exist simultaneously in the object's memory
// layout, and which one you see depends only on the type of the reference
// you're holding, never on the object's actual runtime type.

class Base {
    String label = "Base-label";
}

class Derived extends Base {
    String label = "Derived-label"; // hides Base.label -- does NOT override it
}

class FieldHidingDemo {
    public static void main(String[] args) {
        Derived d = new Derived();
        Base baseRef = d; // same object, two different reference types

        System.out.println("== Field access is resolved by the REFERENCE's declared type, not the object's runtime type ==");
        System.out.println("d.label (declared type Derived)   = " + d.label);
        System.out.println("baseRef.label (declared type Base) = " + baseRef.label + "  (same object as d, but sees Base's field -- fields are hidden, not overridden)");
        System.out.println("((Derived) baseRef).label          = " + ((Derived) baseRef).label + "  (casting the reference back to Derived reveals Derived's field again)");
    }
}
