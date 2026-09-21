import java.util.concurrent.atomic.AtomicInteger;

/** The fix: AtomicInteger.incrementAndGet() is a single, real hardware
 * compare-and-swap operation -- no lost updates possible, by construction. */
public class SafeCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
    public int get() { return count.get(); }
}
