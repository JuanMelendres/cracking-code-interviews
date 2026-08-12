// Java 21. Overriding is resolved dynamically (by the object's actual runtime
// type, via the JVM's invokevirtual instruction and per-class vtable lookup).
// Overloading is resolved statically (by the reference's DECLARED compile-time
// type) -- the compiler picks the overload before the program ever runs, and
// nothing at runtime can change that choice.

class Animal {
    String speak() { return "..."; }
}

class Dog extends Animal {
    @Override
    String speak() { return "Woof"; }
}

class OverrideVsOverloadDemo {
    static String describe(Animal a) { return "some animal: " + a.speak(); }
    static String describe(Dog d) { return "a dog specifically: " + d.speak(); }

    public static void main(String[] args) {
        Animal reference = new Dog(); // declared type Animal, actual object is Dog

        System.out.println("== Overriding: resolved by RUNTIME type ==");
        System.out.println("reference.speak() = " + reference.speak() + "  (Dog's override runs, even though the reference's declared type is Animal)");

        System.out.println();
        System.out.println("== Overloading: resolved by DECLARED (compile-time) type ==");
        System.out.println("describe(reference) = " + describe(reference) + "  (picks the Animal overload -- the compiler never looks at the runtime object)");
        System.out.println("describe((Dog) reference) = " + describe((Dog) reference) + "  (an explicit cast changes the DECLARED type, so overload resolution picks differently -- same object, same runtime type, different compile-time type)");
    }
}
