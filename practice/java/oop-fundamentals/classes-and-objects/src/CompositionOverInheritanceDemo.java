import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates why "favor composition over inheritance" is a real
 * engineering rule, not a style preference — by building the SAME feature
 * (a car that can be electric or gas-powered) two ways, and showing exactly
 * where the inheritance version stops scaling.
 */
public class CompositionOverInheritanceDemo {

    // --- Inheritance version: looks fine until a second, independent axis
    // of variation shows up. ---
    static class Car {
        String describeEngine() {
            return "generic engine";
        }
    }

    static class GasCar extends Car {
        @Override
        String describeEngine() {
            return "gasoline engine";
        }
    }

    static class ElectricCar extends Car {
        @Override
        String describeEngine() {
            return "electric motor";
        }
    }
    // Now add a SECOND independent axis: manual vs. automatic transmission.
    // With inheritance, every combination needs its own subclass:
    // GasManualCar, GasAutomaticCar, ElectricManualCar, ElectricAutomaticCar
    // — 2 axes x 2 options each = 4 classes. A third axis (say, drivetrain:
    // FWD/AWD) makes it 8. The number of subclasses multiplies with every
    // new independent variation, because inheritance can only express "is-a"
    // along a single hierarchy.

    // --- Composition version: each axis is its own small interface, and a
    // Car simply HAS-A one of each, assembled at construction time. ---
    interface Engine {
        String describe();
    }

    interface Transmission {
        String describe();
    }

    static class GasEngine implements Engine {
        public String describe() {
            return "gasoline engine";
        }
    }

    static class ElectricEngine implements Engine {
        public String describe() {
            return "electric motor";
        }
    }

    static class ManualTransmission implements Transmission {
        public String describe() {
            return "manual transmission";
        }
    }

    static class AutomaticTransmission implements Transmission {
        public String describe() {
            return "automatic transmission";
        }
    }

    static class ComposedCar {
        private final Engine engine;
        private final Transmission transmission;

        ComposedCar(Engine engine, Transmission transmission) {
            this.engine = engine;
            this.transmission = transmission;
        }

        String describe() {
            return engine.describe() + " with " + transmission.describe();
        }
    }
    // Adding a third axis here (drivetrain) means adding one more field and
    // one more constructor parameter — zero new classes multiplied against
    // the existing ones. The number of CONCRETE building blocks grows
    // linearly (one new interface + its implementations); the number of
    // combinations you can assemble from them still grows the same way it
    // did before, but you never have to write a class for each combination.

    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(boolean condition, String description) {
        assertions++;
        if (!condition) {
            failures.add(description);
        }
    }

    public static void main(String[] args) {
        // Inheritance version: exactly 2 combinations exist, because exactly
        // 2 subclasses were written.
        Car gas = new GasCar();
        Car electric = new ElectricCar();
        check(gas.describeEngine().equals("gasoline engine"), "GasCar reports a gasoline engine");
        check(electric.describeEngine().equals("electric motor"), "ElectricCar reports an electric motor");

        // Composition version: the SAME two engine types, now freely
        // combined with either transmission at construction time, with no
        // new class written for either combination.
        ComposedCar gasManual = new ComposedCar(new GasEngine(), new ManualTransmission());
        ComposedCar electricAutomatic = new ComposedCar(new ElectricEngine(), new AutomaticTransmission());
        check(gasManual.describe().equals("gasoline engine with manual transmission"),
                "a gas engine can be freely paired with a manual transmission at construction time");
        check(electricAutomatic.describe().equals("electric motor with automatic transmission"),
                "an electric motor can be freely paired with an automatic transmission, same ComposedCar class");

        // The two remaining combinations (electric+manual, gas+automatic)
        // exist too, with zero additional classes — proving the combination
        // count is free with composition, where it cost one subclass each
        // with inheritance.
        ComposedCar electricManual = new ComposedCar(new ElectricEngine(), new ManualTransmission());
        ComposedCar gasAutomatic = new ComposedCar(new GasEngine(), new AutomaticTransmission());
        check(electricManual.describe().equals("electric motor with manual transmission"),
                "the third combination (electric+manual) needs no new class, just a different constructor call");
        check(gasAutomatic.describe().equals("gasoline engine with automatic transmission"),
                "the fourth combination (gas+automatic) needs no new class either — all 4 combinations came from 2 Engine classes + 2 Transmission classes, not 4 Car subclasses");

        System.out.println((failures.isEmpty() ? "PASS" : "FAIL") + " " + (assertions - failures.size()) + "/" + assertions + " assertions");
        for (String f : failures) {
            System.out.println("  FAILED: " + f);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }
}
