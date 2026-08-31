import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

// Real, off-heap memory via the Foreign Function & Memory API (JEP 442,
// third preview in JDK 21; finalized as JEP 454 in JDK 22) -- no
// sun.misc.Unsafe, no JNI. An Arena controls the real lifetime of the
// allocation deterministically, and accessing a MemorySegment after its
// Arena closes throws a real, checked exception instead of crashing or
// reading freed memory silently.
public class OffHeapMemoryDemo {

    public static void main(String[] args) {
        MemorySegment leakedSegment;

        System.out.println("=== Real off-heap allocation and write/read, inside a confined Arena ===");
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT.byteSize());
            segment.set(ValueLayout.JAVA_INT, 0, 42);
            System.out.println("Real value written to real off-heap memory: " + segment.get(ValueLayout.JAVA_INT, 0));
            leakedSegment = segment;
        } // Arena.close() runs here -- the real off-heap memory is really freed.

        System.out.println();
        System.out.println("=== Real safety proof: using the segment AFTER its Arena has closed ===");
        try {
            leakedSegment.get(ValueLayout.JAVA_INT, 0);
            System.out.println("Read succeeded (unexpected -- should have failed)");
        } catch (IllegalStateException e) {
            System.out.println("Real exception thrown instead of a crash or silent garbage read: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Real automatic-lifetime allocation, no explicit close needed ===");
        Arena autoArena = Arena.ofAuto();
        MemorySegment autoSegment = autoArena.allocate(ValueLayout.JAVA_LONG.byteSize());
        autoSegment.set(ValueLayout.JAVA_LONG, 0, 123456789L);
        System.out.println("Real value: " + autoSegment.get(ValueLayout.JAVA_LONG, 0)
                + " (freed automatically once unreachable, like ordinary heap objects)");
    }
}
