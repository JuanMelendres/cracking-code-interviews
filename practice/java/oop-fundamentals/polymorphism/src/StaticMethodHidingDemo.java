// Java 21. Static methods are also NOT polymorphic -- calling a static method
// through an instance reference is resolved by the reference's declared type
// at COMPILE TIME (it compiles to invokestatic, not invokevirtual -- there is
// no vtable lookup at all). A subclass "static override" is really static
// hiding: both methods exist independently, selected by the compile-time type
// of whatever expression precedes the call, never by the object's actual
// runtime class.

class Vehicle {
    static String category() { return "generic vehicle"; }
}

class Car extends Vehicle {
    static String category() { return "car"; } // hides Vehicle.category(), does NOT override it
}

class StaticMethodHidingDemo {
    public static void main(String[] args) {
        Vehicle reference = new Car(); // declared type Vehicle, actual object is Car

        System.out.println("== Static methods are resolved by DECLARED type, never the runtime object ==");
        System.out.println("reference.category() = " + reference.category() + "  (declared type is Vehicle, so Vehicle's static method runs -- compare this to speak() in OverrideVsOverloadDemo, which correctly picked Dog)");
        System.out.println("Car.category()        = " + Car.category());
        System.out.println("Vehicle.category()     = " + Vehicle.category());
    }
}
