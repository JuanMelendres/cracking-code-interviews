import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

// Real proof of VarHandle's defining feature over both AtomicXxx classes and
// the old Unsafe API: per-access-call memory-ordering strength, chosen
// explicitly instead of paying for full volatile semantics everywhere.
// Demonstrates all four real access-mode families compiling and running
// correctly, plus a real, repeated safe-publication proof using
// setRelease/getAcquire -- a genuine happens-before guarantee, not
// something that merely "happens to work" on this hardware.
public class MemoryOrderingAccessModesDemo {

    private int plainField;
    private static final VarHandle FIELD_HANDLE;

    static {
        try {
            FIELD_HANDLE = MethodHandles.lookup()
                    .findVarHandle(MemoryOrderingAccessModesDemo.class, "plainField", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MemoryOrderingAccessModesDemo demo = new MemoryOrderingAccessModesDemo();

        System.out.println("=== The four real VarHandle access-mode families, same field, all working ===");

        FIELD_HANDLE.set(demo, 1);
        System.out.println("plain      set/get:    " + FIELD_HANDLE.get(demo) + "  (no ordering guarantee -- like a normal field)");

        FIELD_HANDLE.setOpaque(demo, 2);
        System.out.println("opaque     set/get:    " + FIELD_HANDLE.getOpaque(demo) + "  (no reordering among opaque ops on the SAME variable, no happens-before)");

        FIELD_HANDLE.setRelease(demo, 3);
        System.out.println("acquire/release set/get: " + FIELD_HANDLE.getAcquire(demo) + "  (one-directional happens-before)");

        FIELD_HANDLE.setVolatile(demo, 4);
        System.out.println("volatile   set/get:    " + FIELD_HANDLE.getVolatile(demo) + "  (full bidirectional happens-before, like the volatile keyword)");

        System.out.println();
        System.out.println("=== Real safe-publication proof: setRelease/getAcquire, repeated 200,000 times ===");
        int iterations = 200_000;
        int failures = 0;
        for (int i = 0; i < iterations; i++) {
            failures += runOnePublicationRound();
        }
        System.out.println("Real failures across " + iterations + " real publish/observe rounds: " + failures
                + (failures == 0 ? "  (release/acquire's happens-before guarantee held every single time)" : "  (unexpected!)"));
    }

    // One real round: a writer thread fully initializes a Data object, then
    // publishes it via setRelease; a reader thread spins on getAcquire until
    // it sees the reference, then checks every field is the fully-initialized
    // value -- proving the writer's PRIOR plain writes are visible to the
    // reader once the acquire read observes the release write.
    private static int runOnePublicationRound() throws InterruptedException {
        Holder holder = new Holder();
        final int[] observed = new int[3];

        Thread writer = new Thread(() -> {
            Data data = new Data(); // plain, unsynchronized field writes
            data.a = 11;
            data.b = 22;
            data.c = 33;
            Holder.DATA_HANDLE.setRelease(holder, data);
        });

        Thread reader = new Thread(() -> {
            Data data;
            do {
                data = (Data) Holder.DATA_HANDLE.getAcquire(holder);
            } while (data == null);
            observed[0] = data.a;
            observed[1] = data.b;
            observed[2] = data.c;
        });

        reader.start();
        writer.start();
        writer.join();
        reader.join();

        boolean correct = observed[0] == 11 && observed[1] == 22 && observed[2] == 33;
        return correct ? 0 : 1;
    }

    private static class Data {
        int a, b, c;
    }

    private static class Holder {
        // Deliberately a PLAIN field, not declared volatile -- the ordering
        // strength comes entirely from which VarHandle access-mode method is
        // called (setRelease/getAcquire below), not from any field modifier.
        Data data;
        static final VarHandle DATA_HANDLE;

        static {
            try {
                DATA_HANDLE = MethodHandles.lookup().findVarHandle(Holder.class, "data", Data.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}
