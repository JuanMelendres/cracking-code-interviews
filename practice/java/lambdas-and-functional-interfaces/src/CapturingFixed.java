import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Real, compiling, executed proof of two ways around the effectively-final
 * capture restriction, and why the restriction exists in the first place:
 * a lambda captures a local variable's VALUE (a copy, at capture time), not
 * a live reference to the variable's storage slot -- so the JLS requires the
 * captured local to never change after capture, or the copy would silently
 * diverge from the "real" variable with no way to detect it.
 */
public class CapturingFixed {

    // A mutable INSTANCE FIELD, unlike a local variable, has no
    // effectively-final restriction -- because the lambda captures `this`
    // (a reference), not a snapshot of the field's value.
    static int instanceStyleCounter = 0;

    public static void main(String[] args) {
        // Fix 1: box the mutable state in a container object. The captured
        // LOCAL VARIABLE (the AtomicInteger reference itself) never changes
        // after capture -- only the object it points to is mutated.
        AtomicInteger boxed = new AtomicInteger(0);
        Supplier<Integer> boxedSupplier = boxed::incrementAndGet;
        System.out.println("Fix 1 (AtomicInteger box): " + boxedSupplier.get() + ", " + boxedSupplier.get() + ", " + boxedSupplier.get());

        // Fix 2: mutate a field instead of a local. Fields are read through
        // `this` (or the class, for static fields) at call time, not
        // snapshotted at capture time.
        Runnable fieldMutator = () -> instanceStyleCounter++;
        fieldMutator.run();
        fieldMutator.run();
        fieldMutator.run();
        System.out.println("Fix 2 (static field mutation, no restriction at all): " + instanceStyleCounter);

        // Proof the restriction is specifically about LOCAL variable
        // reassignment, not about referencing locals at all: this compiles
        // and runs fine because `label` is never reassigned after capture.
        String label = "captured-once";
        Supplier<String> readOnlyCapture = () -> label + "-used-in-lambda";
        System.out.println("Effectively-final (never reassigned) local, no error: " + readOnlyCapture.get());
    }
}
